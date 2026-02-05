package com.portfolio.blog.domain.dto;

import java.util.UUID;

public record Category(
        UUID id,
        String name,
        long postCount
) {
}
