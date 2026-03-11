package de.qf0xb.qticket.problem;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import org.springframework.http.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalErrorHandler {

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
