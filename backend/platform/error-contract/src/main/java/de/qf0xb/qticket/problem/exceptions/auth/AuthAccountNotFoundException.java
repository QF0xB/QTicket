package de.qf0xb.qticket.problem.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthAccountNotFoundException extends ResponseStatusException {
    public AuthAccountNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
