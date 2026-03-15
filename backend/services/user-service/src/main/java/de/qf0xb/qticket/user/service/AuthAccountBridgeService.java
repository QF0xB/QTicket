package de.qf0xb.qticket.user.service;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@NullMarked
public abstract class AuthAccountBridgeService {
    public record KeyPair(String accessToken, String refreshToken, long ttl) { };

    /**
     * Creates a new user account with the provided details.
     *
     * @param username The username for the new account.
     * @param email The email address for the new account.
     * @param password The password for the new account.
     * @param userId The unique identifier for the user creating the account.
     * @return The UUID of the newly created account (null if not created).
     */
    public abstract @Nullable UUID createAccount(String username, String email, String password, UUID userId);
}
