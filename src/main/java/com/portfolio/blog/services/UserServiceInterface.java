package com.portfolio.blog.services;

import com.portfolio.blog.domain.entities.UserEntity;

public interface UserServiceInterface {
    UserEntity getUserFromSecurityContextHolder();
}
