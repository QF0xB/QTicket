package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.auth.repository.RefreshTokenEntityRepository;
import de.qf0xb.qticket.security.rbac.AppPermission;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class TokenService {
    public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60; // 5 minutes
    public static final long REFRESH_TOKEN_VALIDITY_SECONDS = 60 * 60 * 24 * 7; // 1 week

    public record RoleAndPermsInfo(List<String> roles, Set<AppPermission> permissions) { }

    public record TokenPair(String accessToken, String refreshToken, long expiration) { }

    public abstract TokenPair issueTokenPair(AuthAccountEntity account);
    public abstract TokenPair refreshTokenPair(String refreshToken);

    public abstract void revokeRefreshToken(String refreshToken);
}
