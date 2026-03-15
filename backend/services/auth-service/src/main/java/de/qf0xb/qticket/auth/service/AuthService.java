package de.qf0xb.qticket.auth.service;

import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class AuthService {
    public abstract AuthenticationResult authenticate(String login, String password);
}
