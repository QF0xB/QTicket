package de.qf0xb.qticket.user.service.rest;

import de.qf0xb.qticket.auth.v1.client.api.AccountsApi;
import de.qf0xb.qticket.auth.v1.client.model.CreateAccountRequest;
import de.qf0xb.qticket.user.service.AuthAccountBridgeService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@NullMarked
public class RestAuthAccountBridgeService extends AuthAccountBridgeService {
    private final AccountsApi accountsApi;

    public RestAuthAccountBridgeService(AccountsApi accountsApi) {
        this.accountsApi = accountsApi;
    }

    @Override
    public @Nullable UUID createAccount(String username, String email, String password, UUID userId) {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword(password);
        request.setUserId(userId);

        try {
            return accountsApi.createAccount(request).getId();
        } catch (Exception e) {
            log.error("Failed to create account: {}", e.getMessage());
            return null;
        }
    }
}
