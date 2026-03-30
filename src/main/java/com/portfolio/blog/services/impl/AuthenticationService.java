package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.domain.dto.authentication.LoginResponse;
import com.portfolio.blog.domain.dto.authentication.LogoutResponse;
import com.portfolio.blog.domain.dto.authentication.RefreshResponse;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.exceptions.UnauthenticatedException;
import com.portfolio.blog.repositories.UserRepository;
import com.portfolio.blog.services.AuthenticationServiceInterface;
import com.portfolio.blog.services.CookieServiceInterface;
import com.portfolio.blog.services.JwtServiceInterface;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceInterface {

    //Authentication manager is used for password validation;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtServiceInterface jwtService;
    private final CookieServiceInterface cookieService;
    private final UserRepository userRepository;


    @Override
    public UserDetails authenticate(String email, String password) {

        //  Verifying user email and password, exception is thrown if data is invalid;
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (AuthenticationException e) {
            log.warn("| Authentication failed | Exception message - {}  |", e.getMessage());
            throw new UnauthenticatedException("Unauthenticated ");
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

        jwtService.validateRefreshToken(refreshToken); //UnauthenticatedException is thrown if token isn't valid;

        UserDetails userDetails = userDetailsService.loadUserByUsername(
                jwtService.getClaims(refreshToken).getSubject()
        );

        cookieService.removeTokenFromCookie(response);
        jwtService.deleteRefreshToken(refreshToken);

        cookieService.addTokenToCookie(
                jwtService.generateRefreshToken(userDetails),
                response
        );

        return RefreshResponse.builder().token(jwtService.generateToken(userDetails)).build();
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

    @Override
    public UserEntity getUserFromSecurityContextHolder() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserEntity user = auth instanceof AnonymousAuthenticationToken ? null :
                userRepository.findByEmail(auth.getName())
                        .orElseThrow(() -> new UnauthenticatedException("User is not found")
                );
        return user;
    }
}
