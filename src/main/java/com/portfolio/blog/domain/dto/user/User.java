package com.portfolio.blog.domain.dto.user;

import com.portfolio.blog.domain.entities.PostEntity;

import java.util.List;

public record User(
        String username,
        String email,
        List<PostEntity> posts
) {
}
