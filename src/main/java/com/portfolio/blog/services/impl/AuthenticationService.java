package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.domain.dto.authentication.LoginResponse;
import com.portfolio.blog.domain.dto.authentication.LogoutResponse;
import com.portfolio.blog.domain.dto.authentication.RefreshResponse;
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
import org.springframework.security.core.context.SecurityContextHolder;
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


    @Override
    public UserDetails authenticate(String email, String password) {

        //  Verifying user email and password, exception is thrown if data is invalid;
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

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
        String accessToken = jwtService.generateToken(userDetails);
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

        if (jwtService.validateRefreshToken(refreshToken)) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(
                    jwtService.getClaims(refreshToken).getSubject()
            );

            cookieService.removeTokenFromCookie(response);
            jwtService.deleteRefreshToken(refreshToken);

            var newRefreshToken = jwtService.generateRefreshToken(userDetails);
            var newAccessToken = jwtService.generateToken(userDetails);

            cookieService.addTokenToCookie(newRefreshToken, response);

            log.info("User [ {} ] has received new access token ] ", userDetails.getUsername());

            return RefreshResponse.builder().token(newAccessToken).build();

        } else throw new JwtException("Refresh token is expired/invalid");
    }

    @Override
    public LogoutResponse logout(String refreshToken, HttpServletRequest request, HttpServletResponse response) {

        jwtService.deleteRefreshToken(refreshToken); // Removing refresh token from the database
        cookieService.removeTokenFromCookie(response);  // Removing refresh token from the cookies

        String accessToken = jwtService.extractToken(request);

        jwtService.addToBlacklist(accessToken); // Adding access token to the blacklist.

        SecurityContextHolder.getContext().setAuthentication(null);

        log.info("User [ {} ] successfully logged out", jwtService.getClaims(refreshToken).getSubject());

        return new LogoutResponse("Logged out");
    }
}
