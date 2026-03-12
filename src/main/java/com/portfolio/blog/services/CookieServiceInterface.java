package com.portfolio.blog.services;

import jakarta.servlet.http.HttpServletResponse;


public interface CookieServiceInterface {

    void addTokenToCookie(String token, HttpServletResponse response);
    void removeTokenFromCookie(HttpServletResponse response);
}
