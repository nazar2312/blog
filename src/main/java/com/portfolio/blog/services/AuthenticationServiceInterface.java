package com.portfolio.blog.services;

import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationServiceInterface {
    UserDetails authenticate(String email, String password);
    String generateToken(UserDetails details);
    UserDetails validateToken(String token);
}
