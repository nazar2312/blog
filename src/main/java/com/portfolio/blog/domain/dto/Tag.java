package com.portfolio.blog.domain.dto;

import java.util.List;
import java.util.UUID;

public record Tag(
        UUID id,
        String name,
        List<Post> posts
) {
}
