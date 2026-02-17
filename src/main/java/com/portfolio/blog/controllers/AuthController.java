package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.login.LoginRequest;
import com.portfolio.blog.domain.dto.login.LoginResponse;
import com.portfolio.blog.services.AuthenticationServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationServiceInterface authService;

    @PostMapping
    public ResponseEntity<LoginResponse> loginResponseEntity(@RequestBody LoginRequest request) {

        //Authenticate user;
        UserDetails details = authService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        //Generate token;
        String token = authService.generateToken(details);

        //generate response and return;
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .expiresIn(36000)
                .build();
        return ResponseEntity.ok(response);
    }
}
