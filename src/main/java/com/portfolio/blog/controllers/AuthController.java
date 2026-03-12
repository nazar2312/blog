package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.domain.dto.authentication.LoginResponse;import com.portfolio.blog.domain.dto.authentication.RefreshResponse;
import com.portfolio.blog.domain.entities.RefreshToken;
import com.portfolio.blog.repositories.RefreshTokenRepository;
import com.portfolio.blog.services.AuthenticationServiceInterface;
import com.portfolio.blog.services.CookieServiceInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationServiceInterface authService;
    private final CookieServiceInterface cookieService;
    private final RefreshTokenRepository tokenRepository;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse servletResponse) {

        //Authenticate user;
        UserDetails details = authService.authenticate(
                request.getEmail(),
                request.getPassword()
        );

        //Generate token;
        String accessToken = authService.generateToken(details);
        String refreshToken = authService.generateRefreshToken(details);

        cookieService.addTokenToCookie(refreshToken, servletResponse);

        //generate response and return;
        LoginResponse response = LoginResponse.builder()
                .token(accessToken)
                .expiresIn(36000)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/refresh")
    public ResponseEntity<String> refresh (
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletRequest request) {

        if(refreshToken == null) return ResponseEntity.status(401).body("No refresh token");

        Optional<RefreshToken> tokenInDb = tokenRepository.findByToken(refreshToken);

        if(tokenInDb.isEmpty()) return ResponseEntity.status(401).body("No refresh token in db");

        if(refreshToken.equals(tokenInDb.get().getToken())) {

        }
        else return ResponseEntity.status(401).body("Tokens do not match");




        return ResponseEntity.ok().body("");

    }
}
