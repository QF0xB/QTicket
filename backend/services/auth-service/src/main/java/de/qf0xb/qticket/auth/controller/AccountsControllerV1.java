package de.qf0xb.qticket.auth.controller;

import de.qf0xb.qticket.auth.config.AllowedServicesConfig;
import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountStatus;
import de.qf0xb.qticket.auth.service.AuthAccountService;
import de.qf0xb.qticket.auth.service.RbacService;
import de.qf0xb.qticket.auth.service.SearchRequest;
import de.qf0xb.qticket.auth.v1.api.AccountsApi;
import de.qf0xb.qticket.auth.v1.api.model.*;
import de.qf0xb.qticket.problem.exceptions.auth.ForbiddenException;
import de.qf0xb.qticket.problem.exceptions.auth.UnauthorizedException;
import de.qf0xb.qticket.security.CallerIdentity;
import de.qf0xb.qticket.security.CallerIdentityResolver;
import de.qf0xb.qticket.security.rbac.AppPermission;
import de.qf0xb.qticket.security.rbac.RequirePermission;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
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
    private final AllowedServicesConfig allowedServicesConfig;

    private final CallerIdentityResolver callerIdentityResolver = new CallerIdentityResolver();


    public AccountsControllerV1(AuthAccountService authAccountService,
                               RbacService rbacService,
                               AllowedServicesConfig allowedServicesConfig) {
        this.authAccountService = authAccountService;
        this.rbacService = rbacService;
        this.allowedServicesConfig = allowedServicesConfig;
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
    public ResponseEntity<AuthAccount> createAccountForCurrentUser(CreateAccountForCurrentUserRequest createAccountForCurrentUserRequest) {
        AuthAccountEntity account = authAccountService.createAccount(
                createAccountForCurrentUserRequest.getEmail(),
                createAccountForCurrentUserRequest.getUsername(),
                createAccountForCurrentUserRequest.getPassword(),
                UUID.fromString(getJwt().getSubject())
        );

        return ResponseEntity.created(URI.create("/auth/me/accounts")).body(getAccountFromEntity(account));
    }

    @Override
    public ResponseEntity<AuthAccount> createAccount(CreateAccountRequest createAccountRequest) {
        ensureCanCreateAccountForOther();

        AuthAccountEntity account = authAccountService.createAccount(
                createAccountRequest.getEmail(),
                createAccountRequest.getUsername(),
                createAccountRequest.getPassword(),
                createAccountRequest.getUserId()
        );
        return ResponseEntity.created(URI.create("/api/v1/auth/accounts/" + account.getId())).body(getAccountFromEntity(account));
    }

    @Override
    @RequirePermission(AppPermission.USER_SEARCH)
    public ResponseEntity<AuthAccountPage> searchAccounts(@Nullable String q, @Nullable String username, @Nullable String email, @Nullable Boolean enabled, @Nullable Boolean emailVerified, @Nullable Boolean locked, @Nullable UUID userId, String sort, Integer page, Integer size) {
        SearchRequest request = new SearchRequest(
                q, username, email, enabled, emailVerified, locked, userId, sort, page, size
        );

        Page<AuthAccountEntity> result = authAccountService.searchAccounts(request);

        AuthAccountPage pagedResult = new AuthAccountPage();
        pagedResult.setContent(result.getContent().stream().map(this::getAccountFromEntity).toList());
        pagedResult.setTotalElements(result.getTotalElements());
        pagedResult.setTotalPages(result.getTotalPages());
        pagedResult.setSize(result.getSize());
        pagedResult.setNumber(result.getNumber());

        return ResponseEntity.ok(pagedResult);
    }

    @Override
    @RequirePermission(AppPermission.USER_SEARCH)
    public ResponseEntity<AuthAccount> getAccountById(UUID accountId) {

    }

    private void ensureCanCreateAccountForOther() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CallerIdentity caller = callerIdentityResolver.resolve(auth);
        if (caller.isUser()) {
            // JWT user: must have USER_CREATE
            if (!caller.permissions().contains(AppPermission.USER_CREATE.name())) {
                throw new ForbiddenException("Missing permission: USER_CREATE");
            }
            return;
        }
        if (caller.isService()) {
            // X.509 service: CN must be in allowed list
            String serviceName = caller.serviceName();
            if (allowedServicesConfig.getAllowedServiceNamesForAccountCreation()
                    .contains(serviceName)) {
                return;
            }
            throw new ForbiddenException("Service not allowed to create accounts: " + serviceName);
        }
        // Any other auth type is not allowed here
        throw new UnauthorizedException(
                "Create for another user requires JWT with USER_CREATE or allowed service certificate");
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
        account.setStatus(toStatusInfo(status));
        List<String> roleNames = rbacService.getRoleNamesOfUser(entity.getUsername());
        account.setRoles(roleNames);

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
