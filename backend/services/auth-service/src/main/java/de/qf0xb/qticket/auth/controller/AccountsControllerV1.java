package de.qf0xb.qticket.auth.controller;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountStatus;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.v1.api.model.CreateAccountRequest;
import de.qf0xb.qticket.security.rbac.AppPermission;
import de.qf0xb.qticket.auth.v1.api.AccountsApi;
import de.qf0xb.qticket.auth.v1.api.model.AuthAccount;
import de.qf0xb.qticket.auth.v1.api.model.AuthAccountStatusInfo;
import de.qf0xb.qticket.problem.exceptions.auth.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@NullMarked
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AccountsControllerV1 implements AccountsApi {
    private final AuthAccountService authAccountService;
    private final RbacService rbacService;

    public AccountsControllerV1(AuthAccountService authAccountService, RbacService rbacService) {
        this.authAccountService = authAccountService;
        this.rbacService = rbacService;
    }

    @Override
    public ResponseEntity<AuthAccount> getCurrentAccount() {
        Jwt jwt = getJwt();


        String accountIdStr = jwt.getClaimAsString("account_id");
        if (accountIdStr == null) {
            throw new UnauthorizedException("Not authenticated as JWT");
        }

        UUID accountId = UUID.fromString(accountIdStr);
        AuthAccountEntity account = authAccountService.getAccountByAccountId(accountId);
        return ResponseEntity.ok(getAccountFromEntity(account));
    }

    @Override
    public ResponseEntity<List<AuthAccount>> getAllAccountsOfCurrentUser() {
        Jwt jwt = getJwt();

        String userIdStr = jwt.getSubject();
        if (userIdStr == null) {
            throw new UnauthorizedException("Not authenticated as JWT");
        }

        UUID userId = UUID.fromString(userIdStr);
        List<AuthAccountEntity> accounts = authAccountService.getAccountsByUserId(userId);

        return ResponseEntity.ok(accounts.stream().map(this::getAccountFromEntity).toList());
    }

    @Override
    public ResponseEntity<AuthAccount> createAccount(CreateAccountRequest createAccountRequest) {
        AuthAccountEntity account = authAccountService.createAccount(
                createAccountRequest.getEmail(),
                createAccountRequest.getUsername(),
                createAccountRequest.getPassword(),
                UUID.fromString(getJwt().getSubject())
        );

        return ResponseEntity.created(URI.create("/auth/" + account.getId())).body(getAccountFromEntity(account));
    }

    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new UnauthorizedException("Not authenticated as JWT");
        }

        return jwtAuth.getToken();
    }

    private AuthAccount getAccountFromEntity(AuthAccountEntity entity) {
        AuthAccount account = new AuthAccount(
                entity.getId(),
                entity.getUserId(),
                entity.getUsername(),
                entity.getEmail()
        );

        AuthAccountStatus status = entity.getAuthAccountStatus();
        if (status != null) {
            account.setStatus(toStatusInfo(status));
        }
        List<String> roleNames = rbacService.getRoleNamesOfUser(entity.getUsername());
        account.setRoles(roleNames);
        List<String> permissionNames = rbacService.getPermissionsOfUser(entity.getUsername()).stream()
                .map(AppPermission::name)
                .toList();
        account.setPermissions(permissionNames);

        return account;
    }

    private static AuthAccountStatusInfo toStatusInfo(AuthAccountStatus status) {
        AuthAccountStatusInfo info = new AuthAccountStatusInfo();

        info.setEnabled(status.getEnabled());
        info.setEmailVerified(status.getEmailVerified());
        info.setLocked(status.getLocked());

        if (status.getLockedAt() != null) {
            info.setLockedAt(status.getLockedAt().atOffset(ZoneOffset.UTC));
        }
        return info;
    }
}
