package com.portfolio.blog.services.impl;

import com.portfolio.blog.services.CookieServiceInterface;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;


@Service
public class CookieService implements CookieServiceInterface {

    @Override
    public void addTokenToCookie(String token, HttpServletResponse response) {

        Cookie cookie = new Cookie("refreshToken", token);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(10 * 24 * 60 * 60);

        response.addCookie(cookie);
    }

    @Override
    public void removeTokenFromCookie(HttpServletResponse response) {

        Cookie cookie = new Cookie("refreshToken", "" );

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }


}
