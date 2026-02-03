package com.portfolio.blog.mappers.impl;

import com.portfolio.blog.domain.dto.Post;
import com.portfolio.blog.domain.dto.User;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.mappers.PostMapperInterface;
import org.springframework.stereotype.Component;

@Component

public class PostMapper implements PostMapperInterface {
    

    @Override
    public Post toDto(PostEntity postEntity) {
        
        return new Post(
                postEntity.getId(),
                postEntity.getTitle(),
                postEntity.getContent(),
                postEntity.getCreated(),
                postEntity.getUpdated(),
                new User(
                        postEntity.getAuthor().getId(),
                        postEntity.getAuthor().getUsername(),
                        postEntity.getAuthor().getEmail(),
                        postEntity.getAuthor().getPassword(),
                        postEntity.getAuthor().getCreated(),
                        postEntity.getAuthor().getUpdated(),
                        null
                ),
                postEntity.getStatus(),
                postEntity.getReadingTime(),
                postEntity.getCategory(),
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
                new UserEntity(
                        postDto.user().id(),
                        postDto.user().username(),
                        postDto.user().email(),
                        postDto.user().password(),
                        postDto.user().created(),
                        postDto.user().updated(),
                        null
                ),
                postDto.status(),
                postDto.readingTime(),
                postDto.categories(),
                postDto.tags()
        );
    }
}
