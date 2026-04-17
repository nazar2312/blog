package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.domain.dto.authentication.LoginResponse;
import com.portfolio.blog.domain.dto.authentication.LogoutResponse;
import com.portfolio.blog.domain.dto.authentication.RefreshResponse;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.exceptions.UnauthenticatedException;
import com.portfolio.blog.services.AuthenticationServiceInterface;
import com.portfolio.blog.services.CookieServiceInterface;
import com.portfolio.blog.services.JwtServiceInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Instant;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceInterface {

    //Authentication manager is used for password validation;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtServiceInterface jwtService;
    private final CookieServiceInterface cookieService;
    private final UserService userService;

    @Override
    public UserDetails authenticate(String email, String password) {

        //  Verifying user email and password, the exception is thrown if data is invalid;
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            log.warn("| Authentication failed | Exception message - {}  |", e.getMessage());
            throw new UnauthenticatedException("Authentication failed, incorrect username/password");
        }
        //  Returning User Details if username and password is valid (exception is not thrown)
        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse servletResponse) {

        //Authenticate user;
        UserDetails userDetails = authenticate(
                request.getEmail(),
                request.getPassword()
        );

        //Generate token;
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        cookieService.addTokenToCookie(refreshToken, servletResponse);

        log.info("User [ {} ] successfully logged in.", userDetails.getUsername());

        return LoginResponse.builder().token(accessToken).build();
    }

    @Transactional
    @Override
    public RefreshResponse refresh(
            HttpServletRequest request,
            HttpServletResponse response,
            String refreshToken
    ) {

        jwtService.validateRefreshToken(refreshToken); //UnauthenticatedException is thrown if the token isn't valid;

        UserDetails userDetails = userDetailsService.loadUserByUsername(
                jwtService.getClaims(refreshToken).getSubject()
        );

        cookieService.removeTokenFromCookie(response); //Delete from cookies
        jwtService.deleteRefreshToken(refreshToken); //Delete from database

        cookieService.addTokenToCookie(
                jwtService.generateRefreshToken(userDetails),
                response
        );

        return RefreshResponse.builder().token(jwtService.generateAccessToken(userDetails)).build();
    }

    @Override
    public LogoutResponse logout(String refreshToken, HttpServletRequest request, HttpServletResponse response) {

        UserEntity user = userService.getUserFromSecurityContextHolder();

        jwtService.deleteRefreshToken(refreshToken); // Removing refresh token from the database
        cookieService.removeTokenFromCookie(response);  // Removing refresh token from the cookies


        String accessToken = jwtService.extractToken(request);

        if( accessToken != null && !accessToken.isBlank()){
            jwtService.addToBlacklist(accessToken); // Adding an access token to the blacklist.
        }

        log.info("User [ {} ] successfully logged out", user.getEmail());
        SecurityContextHolder.getContext().setAuthentication(null);

        return new LogoutResponse(
                HttpStatus.OK.value(),
                "Successfully logged out",
                Instant.now()
        );
    }
}

