package de.qf0xb.qticket.auth.service.jpa;

import de.qf0xb.qticket.auth.model.account.AuthAccountEntity;
import de.qf0xb.qticket.auth.service.*;
import de.qf0xb.qticket.security.rbac.AppPermission;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class JpaAuthService extends AuthService {
    private final AuthAccountService authAccountService;
    private final RbacService rbacService;
    private final TokenService tokenService;

    public JpaAuthService(AuthAccountService authAccountService, RbacService rbacService, TokenService tokenService) {
        this.authAccountService = authAccountService;
        this.rbacService = rbacService;
        this.tokenService = tokenService;
    }

    @Override
    public AuthenticationResult authenticate(String login, String password) {
        AuthAccountEntity account = authAccountService.authenticate(login, password);

        if (account.isTwoFaRequired()) {
            return new AuthenticationResult(new TwoFaChallengeInfo(null, Set.of()));
        } else {
            return getTokens(account);
        }
    }

    private AuthenticationResult getTokens(AuthAccountEntity account) {
        TokenService.TokenPair pair = tokenService.issueTokenPair(account);
        return new AuthenticationResult(pair.accessToken(), pair.refreshToken(), pair.expiration());
    }
}
