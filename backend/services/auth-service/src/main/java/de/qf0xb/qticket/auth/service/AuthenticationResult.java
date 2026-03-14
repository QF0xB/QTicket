package de.qf0xb.qticket.auth.service;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
public class AuthenticationResult {
    private String accessToken;
    private String refreshToken;
    private Long ttl;
    private TwoFaChallengeInfo twoFaChallengeInfo;

    public AuthenticationResult(TwoFaChallengeInfo twoFaChallengeInfo) {
        this.twoFaChallengeInfo = twoFaChallengeInfo;
    }

    public AuthenticationResult(String accessToken, String refreshToken, Long ttl) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.ttl = ttl;
    }
}
