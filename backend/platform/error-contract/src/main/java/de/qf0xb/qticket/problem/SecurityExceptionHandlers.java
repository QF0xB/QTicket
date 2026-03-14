package de.qf0xb.qticket.problem;

import de.qf0xb.qticket.problem.exceptions.auth.ForbiddenException;
import de.qf0xb.qticket.problem.exceptions.auth.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

public final class SecurityExceptionHandlers {
    private final ObjectMapper objectMapper;
    public SecurityExceptionHandlers(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            UnauthorizedException ex = new UnauthorizedException(
                    authException.getMessage());
            sendProblemDetail(response, request, ex.getStatusCode().value(), "Unauthorized", ex.getReason());
        };
    }
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            ForbiddenException ex = new ForbiddenException(
                    accessDeniedException.getMessage());
            sendProblemDetail(response, request, 403, "Forbidden", ex.getReason());
        };
    }
    private void sendProblemDetail(HttpServletResponse response, HttpServletRequest request,
                                   int status, String title, String detail) throws IOException {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(org.springframework.http.HttpStatus.valueOf(status), detail);
        pd.setTitle(title);
        pd.setInstance(URI.create(request.getRequestURI()));
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), pd);
    }
}
