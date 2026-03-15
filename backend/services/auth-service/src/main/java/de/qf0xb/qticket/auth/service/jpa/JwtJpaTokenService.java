package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.RefreshTokenEntity;
import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.repository.RefreshTokenEntityRepository;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.service.TokenService;
import de.qf0xb.qticket.problem.exceptions.auth.InvalidRefreshTokenException;
import de.qf0xb.qticket.problem.exceptions.auth.ReusedRefreshTokenException;
import de.qf0xb.qticket.security.rbac.AppPermission;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
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
@NullMarked
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

    private RefreshTokenEntity getRefreshTokenEntity(String refreshToken) {
        return refreshTokenEntityRepository.findByCurrentTokenHash(refreshToken).orElseThrow(() ->
                new InvalidRefreshTokenException("Invalid refresh token")
        );
    }

    @Override
    @Transactional
    public TokenPair issueTokenPair(AuthAccountEntity account) {
        String refreshTokenValue = generateRefreshTokenValue();

        // Generate a random, unique family id
        UUID familyId = UUID.randomUUID();
        while (refreshTokenEntityRepository.existsByFamilyId(familyId)) {
            familyId = UUID.randomUUID();
        }

        return buildAndPersistTokenPair(account, familyId, refreshTokenValue, Instant.now());
    }

    @Override
    @Transactional(noRollbackFor = {ReusedRefreshTokenException.class} )
    public TokenPair refreshTokenPair(String refreshToken) {
        Instant invokeTime = Instant.now();

        RefreshTokenEntity entity = getRefreshTokenEntity(refreshToken);
        validateRefreshToken(entity, invokeTime);

        AuthAccountEntity account = authAccountService.getAccountByAccountId(entity.getAccountId());
        authAccountService.isAccountEnabled(account.getUsername());

        String newRefreshToken = rotateRefreshToken(entity, invokeTime);
        return buildAndPersistTokenPair(account, entity.getFamilyId(), newRefreshToken, invokeTime);
    }

    private void validateRefreshToken(RefreshTokenEntity entity, Instant invokeTime) {
        if (entity.isRevoked()) {
            log.debug("Refresh token {} has already been revoked, reuse detected.", entity.getCurrentTokenHash());
            refreshTokenEntityRepository.revokeFamily(entity.getFamilyId(), invokeTime, "reuse");
            throw new ReusedRefreshTokenException("Refresh token has been revoked");
        }

        if(entity.getExpiresAt().isBefore(invokeTime)) {
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }
    }

    private String rotateRefreshToken(RefreshTokenEntity entity, Instant invokeTime) {
        entity.setRevoked(true);
        entity.setRevokedAt(invokeTime);
        entity.setRevokedReason("rotated");
        refreshTokenEntityRepository.save(entity);

        return generateRefreshTokenValue();
    }

    private TokenPair buildAndPersistTokenPair(AuthAccountEntity account, UUID familyId, String refreshTokenValue, Instant invokeTime) {
        Instant accessTokenExpiry = invokeTime.plusSeconds(TokenService.ACCESS_TOKEN_VALIDITY_SECONDS);
        RoleAndPermsInfo rolesAndPerms = getRolesAndPerms(account.getUsername());

        JwtClaimsSet claims = getClaims(account, rolesAndPerms.roles(), rolesAndPerms.permissions(), invokeTime, accessTokenExpiry);

        String accessToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        RefreshTokenEntity refreshTokenEntity = buildRefreshTokenEntity(account, refreshTokenValue, familyId, invokeTime);
        refreshTokenEntityRepository.save(refreshTokenEntity);

        return new TokenPair(accessToken, refreshTokenValue, REFRESH_TOKEN_VALIDITY_SECONDS);
    }

    private JwtClaimsSet getClaims(AuthAccountEntity account, List<String> roles, Set<AppPermission> permissions, Instant issuedAt, Instant expiresAt) {
        return JwtClaimsSet.builder()
                .subject(account.getUserId().toString())
                .claim("account_id", account.getId().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .claim("permissions", permissions.stream().map(AppPermission::name).toList())
                .issuer("auth-service")
                .audience(List.of("qticket"))
                .notBefore(issuedAt)
                .build();
    }

    private RoleAndPermsInfo getRolesAndPerms(String login) {
        List<String> roles = rbacService.getRoleNamesOfUser(login);
        Set<AppPermission> permissions = rbacService.getPermissionsOfUser(login);

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
                .accountId(account.getId())
                .createdAt(invokeTime)
                .currentTokenHash(refreshToken)
                .familyId(familyId)
                .expiresAt(invokeTime.plusSeconds(REFRESH_TOKEN_VALIDITY_SECONDS))
                .build();
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
        Instant invokeTime = Instant.now();

        RefreshTokenEntity entity = getRefreshTokenEntity(refreshToken);
        entity.setRevoked(true);
        entity.setRevokedAt(invokeTime);
        entity.setRevokedReason("logout");
        refreshTokenEntityRepository.save(entity);
    }
}
