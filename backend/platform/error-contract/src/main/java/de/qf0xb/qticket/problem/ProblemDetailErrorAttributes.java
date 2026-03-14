package de.qf0xb.qticket.problem;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Custom ErrorAttributes that returns a map in RFC 7807 Problem Detail shape.
 * BasicErrorController (or your MyErrorController) will use this and render the same
 * layout for all errors, including filter-chain 500s.
 */
@Component
public class ProblemDetailErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> defaultAttributes = super.getErrorAttributes(webRequest, options);

        Integer status = (Integer) defaultAttributes.get("status");
        if (status == null) status = 500;
        String title = (String) defaultAttributes.get("error");
        if (title == null || title.isBlank()) title = HttpStatus.valueOf(status).getReasonPhrase();
        String detail = (String) defaultAttributes.get("message");
        if (detail == null) detail = "An unexpected error occurred";
        String path = (String) defaultAttributes.get("path");
        if (path == null) path = "";

        Map<String, Object> problem = new LinkedHashMap<>();
        problem.put("type", URI.create("about:blank").toString());
        problem.put("title", title);
        problem.put("status", status);
        problem.put("detail", detail);
        problem.put("instance", path);
        return problem;
    }
}