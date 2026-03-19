package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.services.AuthenticationServiceInterface;
import com.portfolio.blog.services.CookieServiceInterface;
import com.portfolio.blog.services.JwtServiceInterface;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceInterface {

    //Authentication manager is used for password validation;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtServiceInterface jwtService;
    private final CookieServiceInterface cookieService;
    private final JwtBlacklistService jwtBlacklistService;


    @Override
    public UserDetails authenticate(String email, String password) {

        //  Verifying user email and password, exception is thrown if data is invalid;
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        //  Returning User Details if username and password is valid (exception is not thrown)
        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    @Transactional
    public String login(LoginRequest request, HttpServletResponse servletResponse) {

        //Authenticate user;
        UserDetails userDetails = authenticate(
                request.getEmail(),
                request.getPassword()
        );

        //Generate token;
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        cookieService.addTokenToCookie(refreshToken, servletResponse);

        log.info("User [ {} ] is successfully logged in.", userDetails.getUsername());
        return accessToken;
    }

    @Transactional
    @Override
    public String refresh(
            HttpServletRequest request,
            HttpServletResponse response,
            String refreshToken
    ) {

        if (jwtService.validateRefreshToken(refreshToken) ) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(
                    jwtService.extractUsername(refreshToken)
            );

            cookieService.removeTokenFromCookie(response);
            jwtService.deleteRefreshToken(refreshToken);

            var newRefreshToken = jwtService.generateRefreshToken(userDetails);
            var newAccessToken = jwtService.generateToken(userDetails);

            cookieService.addTokenToCookie(newRefreshToken, response);

            log.info("User [ {} ] has received new access token ] ", userDetails.getUsername());

            return newAccessToken;

        } else throw new JwtException("Refresh token is expired/invalid");
    }

    @Override
    public void logout(String refreshToken, HttpServletRequest request, HttpServletResponse response) {

        jwtService.deleteRefreshToken(refreshToken);
        cookieService.removeTokenFromCookie(response);


        //I need to make access token invalid using redis for quick access to blacklist of tokens;

        String accessToken = jwtService.extractToken(request);
        jwtBlacklistService.addToBlacklist(accessToken, 84000);

    }


}
