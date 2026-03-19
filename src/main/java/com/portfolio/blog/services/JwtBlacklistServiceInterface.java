package com.portfolio.blog.services;

public interface JwtBlacklistServiceInterface {

    boolean isBlacklisted(String jti);

    void addToBlacklist(String jti, long ttl);
}
