package com.portfolio.blog.domain.dto;

import com.portfolio.blog.domain.entities.CategoryEntity;
import com.portfolio.blog.domain.entities.StatusEntity;
import com.portfolio.blog.domain.entities.TagEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Post(
        UUID id,
        String title,
        String content,
        LocalDateTime created,
        LocalDateTime updated,
        User user,
        StatusEntity status,
        List<CategoryEntity> categories,
        List<TagEntity> tags

) {
}
