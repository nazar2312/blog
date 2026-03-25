package com.portfolio.blog.domain.dto.error;

import lombok.Data;
import org.springframework.http.HttpStatus;
import java.time.Instant;

@Data
public class ApiErrorResponse {

    private String errorCode;
    private int status;
    private String message;
    private Instant timestamp;
    private String path;

    public ApiErrorResponse (HttpStatus status, String message, String path) {
        this.errorCode = status.getReasonPhrase();
        this.status = status.value();
        this.message = message;
        this.timestamp = Instant.now();
        this.path = path;
    }
}
