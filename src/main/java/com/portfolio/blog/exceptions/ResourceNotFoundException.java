package com.portfolio.blog.exceptions;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BlogApiException{

    public ResourceNotFoundException(String resource) {
        super(HttpStatus.NOT_FOUND, "Resource " + resource);
    }
}
