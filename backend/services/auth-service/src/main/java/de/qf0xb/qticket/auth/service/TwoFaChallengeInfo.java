package de.qf0xb.qticket.auth.service;

import de.qf0xb.qticket.auth.v1.api.model.TwoFaMethod;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

@NullMarked
public record TwoFaChallengeInfo(
        UUID challengeId,
        Set<TwoFaMethod> methods
) {
}
