package de.qf0xb.qticket.problem.controller;


import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
  @Override
  public Map<String, Object> getErrorAttributes(
          WebRequest webRequest, ErrorAttributeOptions options) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
    errorAttributes.put("locale", webRequest.getLocale().toString());
    errorAttributes.remove("error");

    System.out.println("Custom error attributes generated: " + errorAttributes);

    return errorAttributes;
  }
}


  /*
    int status = (int) attrs.getOrDefault("status", 500);
    String error = (String) attrs.getOrDefault("error", "Unexpected Error");
    String message = (String) attrs.getOrDefault("message", "An unexpected error occurred");
    String path = (String) attrs.getOrDefault("path", request.getRequestURI());
    ProblemDetail body = ProblemDetail.forStatus(900);
    body.setTitle(error);
    body.setDetail(message);
    body.setInstance(URI.create(path));
    // You can map specific status codes to specific "type" URIs if you want:
    body.setType(
        URI.create(
            switch (HttpStatus.resolve(status)) {
              case NOT_FOUND -> "https://qticket.de/problems/not-found";
              case METHOD_NOT_ALLOWED -> "https://qticket.de/problems/method-not-allowed";
              case null, default -> "about:blank";
            }));
    // Optional: set errorCode, errors, etc. based on attrs
    return ResponseEntity.status(status)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);int status = (int) attrs.getOrDefault("status", 500);
    String error = (String) attrs.getOrDefault("error", "Unexpected Error");
    String message = (String) attrs.getOrDefault("message", "An unexpected error occurred");
    String path = (String) attrs.getOrDefault("path", request.getRequestURI());
    ProblemDetail body = ProblemDetail.forStatus(900);
    body.setTitle(error);
    body.setDetail(message);
    body.setInstance(URI.create(path));
    // You can map specific status codes to specific "type" URIs if you want:
    body.setType(
        URI.create(
            switch (HttpStatus.resolve(status)) {
              case NOT_FOUND -> "https://qticket.de/problems/not-found";
              case METHOD_NOT_ALLOWED -> "https://qticket.de/problems/method-not-allowed";
              case null, default -> "about:blank";
            }));
    // Optional: set errorCode, errors, etc. based on attrs
    return ResponseEntity.status(status)
        .contentType(MediaType.valueOf("application/problem+json"))
        .body(body);
  */
