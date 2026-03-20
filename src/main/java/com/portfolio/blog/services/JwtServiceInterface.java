package com.portfolio.blog.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtServiceInterface {

    String generateRefreshToken(UserDetails details);
    String generateToken(UserDetails details);

    boolean validateRefreshToken(String token);

    void deleteRefreshToken(String refreshToken);

    UserDetails validateToken(String token);
    String extractToken(HttpServletRequest request);
    String extractUsername(String token);

    boolean isBlacklisted(String jti);

    void addToBlacklist(String jti, long ttl);
}
