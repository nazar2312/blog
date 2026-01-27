package com.portfolio.blog.mappers;

import com.portfolio.blog.domain.dto.User;
import com.portfolio.blog.domain.entities.UserEntity;

public interface UserMapperInterface {

    User toDto(UserEntity user);
    UserEntity toEntity(User user);
}
