package com.portfolio.blog.services;

import com.portfolio.blog.TestDataUtil;
import com.portfolio.blog.domain.entities.RefreshToken;
import com.portfolio.blog.exceptions.UnauthenticatedException;
import com.portfolio.blog.repositories.RefreshTokenRepository;
import com.portfolio.blog.security.BlogUserDetails;
import com.portfolio.blog.services.impl.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTests {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private BlogUserDetails userDetails;
    private String token;
    private RefreshToken refreshToken;
    private String invalidToken;

    private final Long refreshExpiry = 8400000L;
    private final String secretKey = "xvdLIGVz4LA6uNb2sw1qorw/9cr9/AM3i77YXSQa1ts=";
    private final Long jwtExpiry = 1000000L;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        this.jwtService = new JwtService(redisTemplate,
                userDetailsService,
                refreshTokenRepository,
                refreshExpiry,
                secretKey,
                jwtExpiry
        );
        this.userDetails = TestDataUtil.generateValidUserDetails();
        this.token = TestDataUtil.generateToken(userDetails, jwtExpiry, secretKey);
        this.refreshToken = TestDataUtil.generateRefreshEntity(token, userDetails, jwtExpiry);
        this.invalidToken = TestDataUtil.generateInvalidToken(userDetails, jwtExpiry);

    }

    @Test
    void generateRefreshToken_shouldReturnToken() {
        // Method is the pure transformation, every negative outcome prevented at the layer above;
        String token = jwtService.generateRefreshToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"));

        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
        // Method is the pure transformation, every negative outcome prevented at the layer above;
    void generateAccessToken_shouldReturnToken () {

        String token = jwtService.generateAccessToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.matches("[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+\\.[A-Za-z0-9_-]+"));
    }

    @Test
    void validateRefreshToken_passes_whenAllConditionsAreMet () {

        RefreshToken refreshTokenEntity = new RefreshToken(
                UUID.randomUUID(),
                token,
                userDetails.getUser(),
                LocalDateTime.now(),
                LocalDateTime.now().plusSeconds(refreshExpiry)
        );

        when(refreshTokenRepository.findByToken(token))
                .thenReturn(Optional.of(refreshTokenEntity));
        assertDoesNotThrow( () -> jwtService.validateRefreshToken(token) );
    }

    @Test
    void validateRefreshToken_throws_whenNotFoundInDatabase() {

        when(refreshTokenRepository.findByToken(token)).thenReturn(
                Optional.empty()
        );
        assertThrows(UnauthenticatedException.class, () -> jwtService.validateRefreshToken(token));
    }

    @Test
    void validateRefreshToken_throws_whenTokenExpired() {
        // Making sure that token is expired;
        refreshToken.setExpiringAt(LocalDateTime.now().minusYears(10));

        when(refreshTokenRepository.findByToken(token))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(UnauthenticatedException.class , () -> jwtService.validateRefreshToken(token));
    }

    @Test
    void validateRefreshToken_throws_whenUserIsLocked() {
        //Making sure that the user is locked;
        refreshToken.getUser().setNonLocked(false);
        when(refreshTokenRepository.findByToken(token))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(UnauthenticatedException.class, () -> jwtService.validateRefreshToken(token));
    }

    @Test
    void validateRefreshToken_throws_whenSignatureFailsValidation () {
        // Making sure that the RefreshToken entity uses invalid token;
        refreshToken.setToken(invalidToken);
        when(refreshTokenRepository.findByToken(invalidToken))
                .thenReturn(Optional.of(refreshToken));

        assertThrows(UnauthenticatedException.class, () -> jwtService.validateRefreshToken(invalidToken));
    }
}
