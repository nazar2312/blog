package com.portfolio.blog.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String email,
        LocalDateTime created,
        LocalDateTime updated,
        List<Post> posts
) {
}
