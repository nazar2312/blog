package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.entities.RefreshToken;
import com.portfolio.blog.repositories.RefreshTokenRepository;
import com.portfolio.blog.repositories.UserRepository;
import com.portfolio.blog.services.JwtServiceInterface;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService implements JwtServiceInterface {

    private final StringRedisTemplate redisTemplate;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;


    @Value("${refresh.expiry}")
    private Long refreshExpiry;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiry}")
    private Long jwtExpiry;


    @Transactional
    @Override
    public String generateRefreshToken(UserDetails details) {

        String token = Jwts.builder()
                .setSubject(details.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(token)
                .user(userRepository.findByEmail(details.getUsername()).get())
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return token;
    }

    @Override
    public String generateToken(UserDetails details) {
        return Jwts.builder()
                .setSubject(details.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean validateRefreshToken(String token) {

        var refresh = refreshTokenRepository.findByToken(token);

        return refresh.isPresent() && LocalDateTime.now().isBefore(refresh.get().getExpiringAt());
    }

    @Override
    @Transactional
    public void deleteRefreshToken(String refreshToken) {

        RefreshToken tokenToDelete = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(EntityNotFoundException::new);
        refreshTokenRepository.delete(tokenToDelete);
    }

    @Override
    public UserDetails validateToken(String token) {

        String username = getClaims(token).getSubject();
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public String extractToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else throw new JwtException("Jwt token wasn't provided");
    }


    @Override
    public void isBlacklisted(String jti) {

        if (redisTemplate.hasKey("blacklist:" + jti)) throw new JwtException("Received token is in the blacklist");

    }

    @Override
    public void addToBlacklist(String jti) {

        redisTemplate.opsForValue().set(
                "blacklist:" + jti,
                "true",
                Duration.ofMillis(calculateTTL(jti))
        );
    }

    @Override
    public Claims getClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) //Validates signature
                .getBody(); //Return claims if token is valid
    }

    private long calculateTTL(String accessToken) {
        // Calculating TTL (time to live) by subtracting current time from token expiration;
        return getClaims(accessToken).getExpiration().getTime() - System.currentTimeMillis();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
