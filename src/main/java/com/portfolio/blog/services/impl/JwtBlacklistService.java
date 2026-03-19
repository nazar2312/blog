package com.portfolio.blog.services.impl;

import com.portfolio.blog.services.JwtBlacklistServiceInterface;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistService implements JwtBlacklistServiceInterface {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean isBlacklisted(String jti) {

        if(redisTemplate.hasKey("blacklist: " + jti)) throw new JwtException("Received token is in the blacklist");

        return false;
    }

    @Override
    public void addToBlacklist(String jti, long ttl) {

        redisTemplate.opsForValue().set(
                "blacklist: " + jti,
                "true",
                ttl,
                TimeUnit.MILLISECONDS
        );
    }

}
