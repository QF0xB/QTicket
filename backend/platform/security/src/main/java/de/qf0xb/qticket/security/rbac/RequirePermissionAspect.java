package de.qf0xb.qticket.security.rbac;

import de.qf0xb.qticket.problem.exceptions.auth.ForbiddenException;
import de.qf0xb.qticket.problem.exceptions.auth.UnauthorizedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequirePermissionAspect {

    @Around("@annotation(requirePermission)")
    public Object check(ProceedingJoinPoint pjp, RequirePermission requirePermission) throws Throwable {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }
        String required = requirePermission.value().name();
        boolean has = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(required::equals);
        if (!has) {
            throw new ForbiddenException("Missing permission: " + required);
        }
        return pjp.proceed();
    }
}