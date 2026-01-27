package com.portfolio.blog.mappers.impl;

import com.portfolio.blog.domain.dto.Post;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.mappers.PostMapperInterface;

public class PostMapper implements PostMapperInterface {
    
    private final UserMapper userMapper;
    
    public PostMapper(UserMapper userMapper) { this.userMapper = userMapper; }

    @Override
    public Post toDto(PostEntity postEntity) {
        
        return new Post(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getCreated(),
                postEntity.getUpdated(),
                userMapper.toDto(
                        postEntity.getUserEntity()
                ),
                postEntity.getStatus(),
                postEntity.getCategories(),
                postEntity.getTags()
        );
    }

    @Override
    public PostEntity toEntity(Post postDto) {

        return new PostEntity(
                postDto.id(),
                postDto.title(),
                postDto.content(),
                postDto.created(),
                postDto.updated(),
                userMapper.toEntity(
                        postDto.user()
                ),
                postDto.status(),
                postDto.categories(),
                postDto.tags()
        );
    }
}
