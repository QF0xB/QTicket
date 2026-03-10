package de.qf0xb.qticket.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

@RestControllerAdvice
public class GlobalErrorHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ProblemDetail handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
    problemDetail.setInstance(URI.create(request.getRequestURI()));
    return problemDetail;
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneric(Exception ex, HttpServletRequest request) {
    HttpStatusCode status = HttpStatusCode.valueOf(500);
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, "Unexpected error");
    pd.setInstance(URI.create(request.getRequestURI()));
    return pd;
  }
}
