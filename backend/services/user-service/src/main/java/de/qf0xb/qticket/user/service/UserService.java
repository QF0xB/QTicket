package de.qf0xb.qticket.user.service;

public abstract class UserService {
    public abstract AuthenticationResult authenticate(String login, String cleartextPassword);
}
