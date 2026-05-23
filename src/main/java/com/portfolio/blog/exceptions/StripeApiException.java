package com.portfolio.blog.exceptions;

import org.springframework.http.HttpStatus;

public class StripeApiException extends BlogApiException {

    public StripeApiException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
