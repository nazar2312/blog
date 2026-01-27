package com.portfolio.blog.mappers;

import com.portfolio.blog.domain.dto.Post;
import com.portfolio.blog.domain.entities.PostEntity;

public interface PostMapperInterface {

    Post toDto(PostEntity post);

    PostEntity toEntity(Post post);
}
