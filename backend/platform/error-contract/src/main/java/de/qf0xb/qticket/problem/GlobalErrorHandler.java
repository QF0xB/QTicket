package de.qf0xb.qticket.problem;

import de.qf0xb.qticket.problem.exceptions.auth.*;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalErrorHandler {

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<ProblemDetail> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(401);
    body.setTitle("Unauthorized");
    body.setDetail(ex.getReason());
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(401)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ProblemDetail> handleForbiddenException(ForbiddenException ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(403);
    body.setTitle("Forbidden");
    body.setDetail(ex.getReason());
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(403)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ProblemDetail handleResponseStatusException(
      ResponseStatusException ex, HttpServletRequest request) {
      ProblemDetail problemDetail =
              ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ProblemDetail> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(ex.getStatusCode());
    body.setTitle("Method ["+ ex.getMethod() + "] Not Allowed");
    body.setDetail("The requested HTTP method ["+ ex.getMethod() + "] is not supported for this endpoint.");
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(405)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(401);
    body.setTitle("Unauthorized");
    body.setDetail(ex.getMessage());
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(401)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler(RoleNotFoundException.class)
  public ResponseEntity<ProblemDetail> handleRoleNotFoundException(RoleNotFoundException ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(404);
    body.setTitle("Role not found");
    body.setDetail(ex.getMessage());
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(404)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler(InvalidRefreshToken.class)
  public ResponseEntity<ProblemDetail> handleInvalidRefreshToken(InvalidRefreshToken ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(401);
    body.setTitle("Invalid refresh token");
    body.setDetail(ex.getMessage());
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(401)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler(ReusedRefreshToken.class)
  public ResponseEntity<ProblemDetail> handleReusedRefreshToken(ReusedRefreshToken ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(401);
    body.setTitle("Invalid refresh token");
    body.setDetail("Invalid refresh token");
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(401)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ProblemDetail> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(403);
    body.setTitle("Access denied");
    body.setDetail(ex.getMessage());
    body.setInstance(URI.create(request.getRequestURI()));
    return ResponseEntity.status(403)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  }

  @ExceptionHandler({RuntimeException.class, Exception.class})
  public ResponseEntity<ProblemDetail> handleExceptions(Exception ex, HttpServletRequest request) {
    ProblemDetail body = ProblemDetail.forStatus(500);
    body.setTitle(ex.getClass().getSimpleName());
    body.setDetail(ex.getMessage());
    // You can map specific status codes to specific "type" URIs if you want:
    body.setType(URI.create("about:blank"));
    body.setInstance(URI.create(request.getRequestURI()));
    // Optional: set errorCode, errors, etc. based on attrs
    return ResponseEntity.status(500)
            .contentType(MediaType.valueOf("application/problem+json"))
            .body(body);
    }
}
