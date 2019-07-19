package de.my5t3ry.alshubapi.response_entity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntityFactory {
    public static <T> ResponseEntity<T> build(final String message,
                                              final ResponseMessageType type,
                                              final T data,
                                              final HttpStatus status) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("message-type", type.name().toLowerCase());
        responseHeaders.set("message", message);
        responseHeaders.set("access-control-expose-headers", "message, message-type");
        return ResponseEntity.status(status).headers(responseHeaders).body(data);
    }
}
