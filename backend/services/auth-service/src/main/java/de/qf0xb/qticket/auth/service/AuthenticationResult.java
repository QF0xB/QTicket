package de.qf0xb.qticket.auth.service;

import lombok.Getter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@Getter
public class AuthenticationResult {
    private @Nullable String accessToken;
    private @Nullable String refreshToken;
    private @Nullable Long ttl;
    private @Nullable TwoFaChallengeInfo twoFaChallengeInfo;

    public AuthenticationResult(TwoFaChallengeInfo twoFaChallengeInfo) {
        this.twoFaChallengeInfo = twoFaChallengeInfo;
    }

    public AuthenticationResult(String accessToken, String refreshToken, Long ttl) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.ttl = ttl;
    }
}
