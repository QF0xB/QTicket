package de.qf0xb.qticket.problem;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.ErrorAttributes;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.Map;

@Controller
@RequestMapping("${server.error.path:${error.path:/error}}")
public class ProblemDetailErrorController implements ErrorController {

    private final ErrorAttributes errorAttributes;

    public ProblemDetailErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProblemDetail> error(HttpServletRequest request) {
        WebRequest webRequest = new ServletWebRequest(request);
        Map<String, Object> attrs = errorAttributes.getErrorAttributes(
                webRequest, ErrorAttributeOptions.defaults());

        Integer statusCode = (Integer) attrs.get("status");
        HttpStatus status = statusCode != null
                ? HttpStatus.valueOf(statusCode)
                : HttpStatus.INTERNAL_SERVER_ERROR;
        String detail = (String) attrs.getOrDefault("message", "An unexpected error occurred");
        String title = (String) attrs.getOrDefault("error", status.getReasonPhrase());
        String path = (String) attrs.getOrDefault("path", request.getRequestURI());

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setTitle(title != null && !title.isBlank() ? title : status.getReasonPhrase());
        pd.setInstance(URI.create(path != null ? path : request.getRequestURI()));
        pd.setType(URI.create("about:blank"));

        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(pd);
    }
}