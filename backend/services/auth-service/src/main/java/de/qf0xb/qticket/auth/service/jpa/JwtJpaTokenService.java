package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.RefreshTokenEntity;
import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.auth.repository.RefreshTokenEntityRepository;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.service.TokenService;
import de.qf0xb.qticket.problem.exceptions.auth.InvalidRefreshToken;
import de.qf0xb.qticket.problem.exceptions.auth.ReusedRefreshToken;
import de.qf0xb.qticket.security.rbac.AppPermission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class JwtJpaTokenService extends TokenService {
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;
    private final JwtEncoder jwtEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    private final AuthAccountService authAccountService;
    private final RbacService rbacService;

    public JwtJpaTokenService(RefreshTokenEntityRepository refreshTokenEntityRepository, JwtEncoder jwtEncoder, AuthAccountService authAccountService, RbacService rbacService) {
        this.refreshTokenEntityRepository = refreshTokenEntityRepository;
        this.jwtEncoder = jwtEncoder;
        this.authAccountService = authAccountService;
        this.rbacService = rbacService;
    }

    @Override
    @Transactional
    public TokenPair issueTokenPair(AuthAccountEntity account) {
        String refreshTokenValue = generateRefreshTokenValue();
        RoleAndPermsInfo rolesAndPerms = getRolesAndPerms(account);

        UUID familyId = UUID.randomUUID();
        while (refreshTokenEntityRepository.existsByFamilyId(familyId)) {
            familyId = UUID.randomUUID();
        }

        return generateTokenPair(account, rolesAndPerms.roles(), rolesAndPerms.permissions(), familyId, refreshTokenValue, Instant.now());
    }



    @Override
    @Transactional(noRollbackFor = {ReusedRefreshToken.class} )
    public TokenPair refreshTokenPair(String refreshToken) {
        Instant invokeTime = Instant.now();

        RefreshTokenEntity entity = refreshTokenEntityRepository.findByCurrentTokenHash(refreshToken).orElseThrow(() ->
                new InvalidRefreshToken("Invalid refresh token")
        );

        if (entity.getFamilyId() == null) {
            throw new InvalidRefreshToken("Refresh token has no family id");
        }

        if (entity.isRevoked()) {
            log.info("Refresh token has already been revoked, reuse detected: {}", refreshToken);
            refreshTokenEntityRepository.revokeFamily(entity.getFamilyId(), invokeTime, "reuse");
            throw new ReusedRefreshToken("Refresh token has been revoked");
        }

        if(entity.getExpiresAt().isBefore(invokeTime)) {
            throw new InvalidRefreshToken("Refresh token has expired");
        }

        if (entity.getUserId() == null) {
            throw new InvalidRefreshToken("Refresh token has no user id");
        }

        AuthAccountEntity account = authAccountService.getAccountById(entity.getUserId());

        if(!authAccountService.isAccountEnabled(account)) {
            throw new DisabledException("Account is disabled");
        }

        RoleAndPermsInfo rolesAndPerms = getRolesAndPerms(account);

        String newRefreshToken = generateRefreshTokenValue();
        entity.setRevoked(true);
        entity.setRevokedAt(invokeTime);
        entity.setRevokedReason("rotated");
        refreshTokenEntityRepository.save(entity);

        return generateTokenPair(account, rolesAndPerms.roles(), rolesAndPerms.permissions(), entity.getFamilyId(), newRefreshToken, invokeTime);
    }

    @Override
    public void revokeRefreshToken(String refreshToken) {
        Instant invokeTime = Instant.now();

        RefreshTokenEntity entity = refreshTokenEntityRepository.findByCurrentTokenHash(refreshToken).orElseThrow(() ->
                new InvalidRefreshToken("Invalid refresh token")
        );

        entity.setRevoked(true);
        entity.setRevokedAt(invokeTime);
        entity.setRevokedReason("logout");
        refreshTokenEntityRepository.save(entity);
    }

    private TokenPair generateTokenPair(AuthAccountEntity account, List<String> roles, Set<AppPermission> permissions, UUID familyId, String refreshTokenValue, Instant invokeTime) {
        Instant accessTokenExpiry = invokeTime.plusSeconds(TokenService.ACCESS_TOKEN_VALIDITY_SECONDS);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(account.getId().toString())
                .issuedAt(invokeTime)
                .expiresAt(accessTokenExpiry)
                .claim("roles", roles)
                .claim("permissions", permissions.stream().map(AppPermission::name).toList())
                .issuer("auth-service")
                .audience(List.of("qticket"))
                .notBefore(invokeTime)
                .build();

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        RefreshTokenEntity refreshTokenEntity = buildRefreshTokenEntity(account, refreshTokenValue, familyId, invokeTime.plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS));

        refreshTokenEntityRepository.save(refreshTokenEntity);
        return new TokenPair(accessToken, refreshTokenValue, REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    private RoleAndPermsInfo getRolesAndPerms(AuthAccountEntity account) {
        List<String> roles = rbacService.getRoleNamesOfUser(account);
        Set<AppPermission> permissions = rbacService.getPermissionsOfUser(account);

        return new RoleAndPermsInfo(roles, permissions);
    }

    private String generateRefreshTokenValue() {
        while (true) {
            byte[] bytes = new byte[32];
            secureRandom.nextBytes(bytes);
            String refreshTokenValue = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

            if (!refreshTokenEntityRepository.existsByCurrentTokenHash(refreshTokenValue))
                return refreshTokenValue;
        }
    }

    private RefreshTokenEntity buildRefreshTokenEntity(AuthAccountEntity account, String refreshToken, UUID familyId, Instant invokeTime) {
        return RefreshTokenEntity.builder()
                .userId(account.getId())
                .createdAt(invokeTime)
                .currentTokenHash(refreshToken)
                .familyId(familyId)
                .expiresAt(invokeTime.plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS))
                .build();
    }
}
