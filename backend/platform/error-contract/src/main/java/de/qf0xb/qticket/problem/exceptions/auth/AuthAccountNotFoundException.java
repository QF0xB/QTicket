package de.qf0xb.qticket.problem.exceptions.auth;

public class AuthAccountNotFoundException extends RuntimeException {
    public AuthAccountNotFoundException(String message) {
        super(message);
    }
}
