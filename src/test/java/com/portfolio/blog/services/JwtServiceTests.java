package com.portfolio.blog.services;

import com.portfolio.blog.TestDataUtil;
import com.portfolio.blog.domain.entities.RefreshToken;
import com.portfolio.blog.exceptions.UnauthenticatedException;
import com.portfolio.blog.repositories.RefreshTokenRepository;
import com.portfolio.blog.repositories.UserRepository;
import com.portfolio.blog.security.BlogUserDetails;
import com.portfolio.blog.services.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class JwtServiceTests {

    @Autowired
    JwtService jwtService;

    @Value("${refresh.expiry}")
    private Long refreshExpiry;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiry}")
    private Long jwtExpiry;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    @MockitoBean
    private UserRepository userRepository;

    private BlogUserDetails validUser;

    @BeforeEach
    void setUp() {
        this.validUser = TestDataUtil.generateValidUserDetails();
    }

    @Test // Method is the pure transformation, every negative outcome prevented at the layer above;
    void generateRefreshToken_shouldReturnToken() {

        var user = validUser;

        String token = jwtService.generateRefreshToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"));

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test // Method is the pure transformation, every negative outcome prevented at the layer above;
    void generateAccessToken_shouldReturnToken(){

        var user = TestDataUtil.generateValidUserDetails();

        String token = jwtService.generateAccessToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"));
    }

    @Test
    void validateRefreshToken_passes_whenAllConditionsAreMet() {

        String token = TestDataUtil.generateToken(validUser, refreshExpiry, secretKey);

        RefreshToken refreshTokenEntity = new RefreshToken(
                UUID.randomUUID(),
                token,
                validUser.getUser(),
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(refreshExpiry)
        );

        when(refreshTokenRepository.findByToken(token))
                .thenReturn(Optional.of(refreshTokenEntity));

        jwtService.validateRefreshToken(token);
        assertDoesNotThrow(() -> UnauthenticatedException.class);

    }









}
