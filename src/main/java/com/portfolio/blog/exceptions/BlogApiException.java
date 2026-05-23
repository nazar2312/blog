package com.portfolio.blog.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BlogApiException extends RuntimeException{

    private final HttpStatus status;

    protected BlogApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
