package com.portfolio.blog.mappers.impl;

import com.portfolio.blog.domain.dto.User;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.mappers.UserMapperInterface;

import java.util.Optional;

public class UserMapper implements UserMapperInterface {
    
    private final PostMapper postMapper;
    
    public UserMapper(PostMapper postMapper) { this.postMapper = postMapper; }
    
    @Override
    public User toDto(UserEntity userEntity) {
        
        return new User(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getCreated(),
                userEntity.getUpdated(),
                Optional.ofNullable(userEntity.getPosts())
                        .map(posts -> posts.stream()
                                .map(postMapper::toDto)
                                .toList()
                ).orElse(null)
        );
    }

    @Override
    public UserEntity toEntity(User userDto) {
        
        return new UserEntity(
                userDto.id(),
                userDto.username(),
                userDto.email(),
                userDto.password(),
                userDto.created(),
                userDto.updated(),
                Optional.ofNullable(userDto.posts())
                        .map(user -> user.stream()
                                .map(postMapper::toEntity)
                                .toList())
                        .orElse(null)
        );
    }



}
