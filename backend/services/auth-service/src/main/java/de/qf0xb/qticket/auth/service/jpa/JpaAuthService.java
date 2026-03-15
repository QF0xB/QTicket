package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.service.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@NullMarked
public class JpaAuthService extends AuthService {
    private final AuthAccountService authAccountService;
    private final TokenService tokenService;

    public JpaAuthService(AuthAccountService authAccountService, TokenService tokenService) {
        this.authAccountService = authAccountService;
        this.tokenService = tokenService;
    }

    @Override
    public AuthenticationResult authenticate(String login, String password) {
        AuthAccountEntity account = authAccountService.authenticate(login, password);

        if (account.isTwoFaRequired()) {
            return new AuthenticationResult(new TwoFaChallengeInfo(UUID.randomUUID(), Set.of())); // TODO: implement 2fa
        } else {
            return getTokens(account);
        }
    }

    private AuthenticationResult getTokens(AuthAccountEntity account) {
        TokenService.TokenPair pair = tokenService.issueTokenPair(account);
        return new AuthenticationResult(pair.accessToken(), pair.refreshToken(), pair.expiration());
    }
}
