package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.authentication.LoginRequest;
import com.portfolio.blog.domain.dto.authentication.LoginResponse;
import com.portfolio.blog.domain.dto.authentication.LogoutResponse;
import com.portfolio.blog.domain.dto.authentication.RefreshResponse;
import com.portfolio.blog.domain.entities.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationServiceInterface {
    UserDetails authenticate(String email, String password);

    LoginResponse login(LoginRequest request, HttpServletResponse servletResponse);

    RefreshResponse refresh(HttpServletRequest request, HttpServletResponse response, String refreshToken);

    LogoutResponse logout(String refreshToken, HttpServletRequest request, HttpServletResponse response);

    UserEntity getUserFromSecurityContextHolder();
}
