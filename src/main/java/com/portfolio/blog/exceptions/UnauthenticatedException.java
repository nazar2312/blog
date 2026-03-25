package com.portfolio.blog.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthenticatedException extends BlogApiException {

    public UnauthenticatedException(String message) {
        super(HttpStatus.UNAUTHORIZED, message);
    }
}
