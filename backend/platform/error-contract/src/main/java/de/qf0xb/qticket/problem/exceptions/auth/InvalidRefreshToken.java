package de.qf0xb.qticket.problem.exceptions.auth;

public class InvalidRefreshToken extends RuntimeException {
    public InvalidRefreshToken(String message) {
        super(message);
    }
}
