package com.portfolio.blog;

import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.security.BlogUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public class TestDataUtil {

    public static BlogUserDetails generateValidUserDetails() {

        UserEntity user = UserEntity.builder()
                .id(UUID.randomUUID())
                .username("testUsername")
                .email("email@gmail.com")
                .password("password")
                .nonLocked(true)
                .role(Role.USER)
                .created(LocalDateTime.now())
                .updated(LocalDateTime.now())
                .build();

        return new BlogUserDetails(user);
    }

    public static String generateToken(BlogUserDetails details, long jwtExpiry, String secretKey) {

        return Jwts.builder()
                .subject(details.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiry))
                .claim("role", details.getUser().getRole()) // UserEntity has (nullable = false), so, roles cannot be null;
                .signWith(getSigningKey(secretKey))
                .compact();
    }
    private static SecretKey getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
