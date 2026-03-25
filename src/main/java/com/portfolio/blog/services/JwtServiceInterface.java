package com.portfolio.blog.services;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtServiceInterface {

    // Refresh token operations
    String generateRefreshToken(UserDetails details);
    void validateRefreshToken(String token);
    void deleteRefreshToken(String refreshToken);

    //  Access token operations
    String generateToken(UserDetails details);
    UserDetails validateToken(String token);
    String extractToken(HttpServletRequest request);
    Claims getClaims(String token);


    void isBlacklisted(String jti);
    void addToBlacklist(String jti);

}
