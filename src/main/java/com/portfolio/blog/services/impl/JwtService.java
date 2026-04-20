package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.RefreshToken;
import com.portfolio.blog.exceptions.UnauthenticatedException;
import com.portfolio.blog.repositories.RefreshTokenRepository;
import com.portfolio.blog.security.BlogUserDetails;
import com.portfolio.blog.services.JwtServiceInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
public class JwtService implements JwtServiceInterface {

    private final StringRedisTemplate redisTemplate;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private final Long refreshExpiry;
    private final String secretKey;
    private final Long jwtExpiry;

    public JwtService(
            StringRedisTemplate redisTemplate,
            UserDetailsService userDetailsService,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${refresh.expiry}") Long refreshExpiry,
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiry}") Long jwtExpiry
    ) {
        this.redisTemplate = redisTemplate;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpiry = refreshExpiry;
        this.secretKey = secretKey;
        this.jwtExpiry = jwtExpiry;
    }


    @Override
    public String generateRefreshToken(@NotNull UserDetails details) {

        var user = (BlogUserDetails) details;

        String token = Jwts.builder()
                .subject(user.getUser().getEmail()) // UserEntity has (nullable = false), so, email cannot be null;
                .expiration(new Date(System.currentTimeMillis() + refreshExpiry))
                .signWith(getSigningKey())
                .compact();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user.getUser())
                .expiringAt(LocalDateTime.now().plusSeconds(refreshExpiry / 1000))
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

    @Override
    public String generateAccessToken(@NotNull UserDetails details) {

        var user = (BlogUserDetails) details;

        return Jwts.builder()
                .subject(details.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiry))
                .claim("role", user.getUser().getRole()) // UserEntity has (nullable = false), so, roles cannot be null;
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public void validateRefreshToken(String token) {

        var refresh = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UnauthenticatedException("Refresh token is not valid / expired"));

        if (LocalDateTime.now().isAfter(refresh.getExpiringAt())
                || !refresh.getUser().isNonLocked()
        ) {
            throw new UnauthenticatedException("Refresh token is not valid / expired");
        }

        // Validate signature;
        try {
            getClaims(refresh.getToken());
        } catch (JwtException e) {
            throw new UnauthenticatedException("Refresh token is not valid / expired");
        }
    }

    @Override
    public void deleteRefreshToken(String refreshToken) {

        refreshTokenRepository.deleteByToken(refreshToken);
    }

    @Override
    public UserDetails validateToken(String token) {

        try {
            String username = getClaims(token).getSubject(); //Return claims if the token is valid
            var user = userDetailsService.loadUserByUsername(username);

            if (user.isAccountNonExpired() && user.isAccountNonLocked())
                return user;
            else
                throw new JwtException("Access token is not valid / expired");
        } catch (JwtException e) {
            throw new UnauthenticatedException(e.getMessage());
        }
    }

    @Override
    public String extractToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }


    @Override
    public void isBlacklisted(String jti) {

        if (redisTemplate.hasKey("blacklist:" + jti))
            throw new UnauthenticatedException("Token is in the blacklist");
    }

    @Override
    public void addToBlacklist(String jti) {

        long ttl = 0;

        try {
            ttl = getClaims(jti).getExpiration().getTime() - System.currentTimeMillis();
            if (ttl < 0) ttl = 0;
        } catch (JwtException ignored) {}

        redisTemplate.opsForValue().set(
                "blacklist:" + jti,
                "true",
                Duration.ofMillis(ttl)
        );
    }

    @Override
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
