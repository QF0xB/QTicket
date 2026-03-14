package de.qf0xb.qticket.problem.exceptions.auth;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
