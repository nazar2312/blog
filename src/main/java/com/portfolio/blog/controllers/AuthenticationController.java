package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.domain.dto.authentication.LoginResponse;
import com.portfolio.blog.domain.dto.authentication.LogoutResponse;
import com.portfolio.blog.domain.dto.authentication.RefreshResponse;
import com.portfolio.blog.services.AuthenticationServiceInterface;
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

    private final AuthenticationServiceInterface authenticationService;

    @PostMapping(path = "/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse servletResponse
    ) {
        LoginResponse loginResponse = authenticationService.login(request, servletResponse);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping(path = "/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @CookieValue(name = "refreshToken")
            String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        RefreshResponse refreshResponse = authenticationService.refresh(request, response, refreshToken);
        return ResponseEntity.ok(refreshResponse);

    }

    @PostMapping(path = "/logout")
    public ResponseEntity<LogoutResponse> logout(
            @CookieValue(name = "refreshToken")
            String refreshToken,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        LogoutResponse logoutResponse = authenticationService.logout(refreshToken, request, response);

        return ResponseEntity.ok(logoutResponse);
    }
}
