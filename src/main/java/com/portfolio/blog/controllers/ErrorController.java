package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.error.ApiErrorResponse;
import com.portfolio.blog.exceptions.BlogApiException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@ControllerAdvice
@Slf4j
public class ErrorController {

//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ApiErrorResponse> handleAccessDenied(AccessDeniedException ex) {
//
//        ApiErrorResponse response = ApiErrorResponse.builder()
//                .status(HttpStatus.UNAUTHORIZED.value())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//    }
//
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ApiErrorResponse> handleJwtException(AuthenticationException ex) {
//        ApiErrorResponse response = ApiErrorResponse.builder()
//                .status(HttpStatus.FORBIDDEN.value())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//    }
//
//    @ExceptionHandler(JwtException.class)
//    public ResponseEntity<ApiErrorResponse> handleJwtException(JwtException ex) {
//
//        ApiErrorResponse response = ApiErrorResponse.builder()
//                .status(HttpStatus.FORBIDDEN.value())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<ApiErrorResponse> handleIllegalArg(IllegalArgumentException ex) {
//
//        ApiErrorResponse response = ApiErrorResponse.builder()
//                .status(HttpStatus.BAD_REQUEST.value())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler(BadCredentialsException.class)
//    public ResponseEntity<ApiErrorResponse> handleBadCredentials(BadCredentialsException ex) {
//
//        ApiErrorResponse response = ApiErrorResponse.builder()
//                .status(HttpStatus.FORBIDDEN.value())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
//    }
//
//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<ApiErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
//
//        ApiErrorResponse response = ApiErrorResponse.builder()
//                .status(HttpStatus.NOT_FOUND.value())
//                .message(ex.getMessage())
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//    }
//


    // Bean validation failure.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed at {} : {} , ", request.getRequestURI(), message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ApiErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request.getRequestURI())
        );
    }

    // Custom API exceptions.
    @ExceptionHandler(BlogApiException.class)
    public ResponseEntity<ApiErrorResponse> handleBlogApiException(
            BlogApiException ex,
            HttpServletRequest request) {

        if (ex.getStatus().is5xxServerError()) {
            log.error("Server error at {} : {} ", request.getRequestURI(), ex.getMessage());
        } else {
            log.error("Client error at {} : {} ", request.getRequestURI(), ex.getMessage());
        }
        return ResponseEntity.status(ex.getStatus())
                .body(new ApiErrorResponse(
                        ex.getStatus(),
                        ex.getMessage(),
                        request.getRequestURI())
                );
    }

    // Malformed request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        log.error("Unreadable body at: {} ", request.getRequestURI());

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Malformed request body",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Unexpected
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {

        log.error("Unexpected exception occurred at: {}", request.getRequestURI());

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Unexpected error occurred",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}

