package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.v1.api.model.TwoFaMethod;

import java.util.Set;
import java.util.UUID;

public record TwoFaChallengeInfo(
        UUID challengeId,
        Set<TwoFaMethod> methods
) {
}
