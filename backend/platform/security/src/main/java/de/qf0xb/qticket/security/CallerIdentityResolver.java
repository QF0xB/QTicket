package de.qf0xb.qticket.security;

import de.qf0xb.qticket.problem.exceptions.auth.UnauthorizedException;
import de.qf0xb.qticket.security.rbac.AppPermission;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Resolves a Spring Security {@link Authentication} into a normalized {@link CallerIdentity}.
 * <p>
 * Conventions:
 * - User calls (JWT): {@link JwtAuthenticationToken} with sub = userId and permissions mapped to authorities.
 * - Service calls (mTLS): {@link PreAuthenticatedAuthenticationToken} whose principal is a {@link UserDetails}
 *   created from the X.509 client certificate CN. The username is treated as the service name.
 */
@NullMarked
@Component
public class CallerIdentityResolver {

    /**
     * Resolve the current caller based on the given {@link Authentication}.
     * Throws {@link UnauthorizedException} if {@code auth} is null or not authenticated.
     */
    public CallerIdentity resolve(@Nullable Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return fromJwt(jwtAuth);
        }

        if (auth instanceof PreAuthenticatedAuthenticationToken preAuth) {
            return fromPreAuthenticated(preAuth);
        }

        // Fallback: authenticated but unsupported type (e.g. basic auth)
        return new CallerIdentity(false, null, null, Set.of(), false, null);
    }

    private static CallerIdentity fromJwt(JwtAuthenticationToken jwtAuth) {
        Jwt jwt = jwtAuth.getToken();
        String sub = jwt.getSubject();
        if (sub == null) {
            throw new UnauthorizedException("JWT subject (userId) is missing");
        }
        UUID userId = UUID.fromString(sub);

        Set<String> permissions = jwtAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a != null && !a.startsWith("FACTOR_"))
                .collect(Collectors.toUnmodifiableSet());

        return new CallerIdentity(
                true,
                userId,
                jwt,
                permissions,
                false,
                null
        );
    }

    private static CallerIdentity fromPreAuthenticated(PreAuthenticatedAuthenticationToken preAuth) {
        Object principal = preAuth.getPrincipal();
        String serviceName = null;

        if (principal instanceof UserDetails userDetails) {
            serviceName = userDetails.getUsername();
        } else {
            serviceName = principal.toString();
        }

        return new CallerIdentity(
                false,
                null,
                null,
                Set.of(),
                serviceName != null,
                serviceName
        );
    }
}

