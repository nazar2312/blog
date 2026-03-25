package com.portfolio.blog.exceptions;

import org.springframework.http.HttpStatus;

public class BlogApiException extends RuntimeException{

    private final HttpStatus status;

    protected BlogApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
