package de.qf0xb.qticket.problem.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class ReusedRefreshTokenException extends ResponseStatusException {
    public ReusedRefreshTokenException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
