package com.portfolio.blog.services.impl;

import com.portfolio.blog.services.AuthenticationServiceInterface;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements AuthenticationServiceInterface {

    //Authentication manager is used for password validation;
    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secretKey;

    private final Long jwtExpiry = 3600000L;

    @Override
    public UserDetails authenticate(String email, String password) {

        //  Verifying user email and password, exception is thrown if data is invalid;
        authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        //  Returning User Details if username and password is valid (exception is not thrown)
        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    public String generateToken(UserDetails details) {
        return Jwts.builder()
                .setClaims(null)
                .setSubject(details.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiry))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    public String extractUsername (String token) {

        //  Throws exception in case if token isn't valid;
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) //Validates signature
                .getBody() //Return claims if token is valid
                .getSubject(); //Returns username
    }

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");

        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        } else throw new BadCredentialsException("Jwt token wasn't provided");
    }
}
