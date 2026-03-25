package com.portfolio.blog.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BlogApiException {

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
