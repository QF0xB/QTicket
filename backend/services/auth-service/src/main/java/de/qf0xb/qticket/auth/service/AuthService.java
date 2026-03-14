package de.qf0xb.qticket.auth.service;

public abstract class AuthService {
    public abstract AuthenticationResult authenticate(String login, String password);
}
