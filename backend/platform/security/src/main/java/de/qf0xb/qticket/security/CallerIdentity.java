package de.qf0xb.qticket.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;
import java.util.UUID;

/**
 * Normalized view of the current caller.
 *
 * Under {@code @NullMarked}:
 * - For user calls (JWT), {@code isUser == true}, {@code userId} and {@code jwt} are non-null.
 * - For service calls (mTLS), {@code isService == true}, {@code serviceName} is non-null.
 * - For unsupported/anonymous, both flags are false and the nullable fields are null.
 */
public record CallerIdentity(
        // User information
        boolean isUser,
        @Nullable UUID userId,
        @Nullable Jwt jwt,
        Set<String> permissions,

        // Service information
        boolean isService,
        @Nullable String serviceName
) { }
