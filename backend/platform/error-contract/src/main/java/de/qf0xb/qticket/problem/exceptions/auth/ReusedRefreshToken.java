package de.qf0xb.qticket.problem.exceptions.auth;

public class ReusedRefreshToken extends RuntimeException {
    public ReusedRefreshToken(String message) {
        super(message);
    }
}
