package de.qf0xb.qticket.problem.exceptions.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailUnverifiedException extends ResponseStatusException {
    public EmailUnverifiedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
