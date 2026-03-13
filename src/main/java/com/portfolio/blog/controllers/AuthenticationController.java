package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.domain.dto.authentication.LoginResponse;
import com.portfolio.blog.domain.dto.authentication.RefreshResponse;
import com.portfolio.blog.services.impl.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse servletResponse) {

        String accessToken = authenticationService.login(request,servletResponse);

        return ResponseEntity.ok(LoginResponse.builder()
                .token(accessToken)
                .build()
        );
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<RefreshResponse> refresh (
            @CookieValue(name = "refreshToken")
            String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        String access = authenticationService.refresh(request, response, refreshToken);

        return ResponseEntity.ok(new RefreshResponse(access));

    }

    @PostMapping(path = "/logout")
    public void logout(
            @CookieValue(name = "refreshToken")
            String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        authenticationService.logout(refreshToken, request, response);


    }
}
