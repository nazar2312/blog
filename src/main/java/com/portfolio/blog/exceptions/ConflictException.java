package com.portfolio.blog.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends BlogApiException{

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
