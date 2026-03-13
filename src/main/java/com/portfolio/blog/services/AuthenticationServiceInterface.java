package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationServiceInterface {
    UserDetails authenticate(String email, String password);

    String login(LoginRequest request, HttpServletResponse servletResponse);

    String refresh(HttpServletRequest request, HttpServletResponse response, String refreshToken);

    void logout(String refreshToken, HttpServletRequest request, HttpServletResponse response);
}
