package com.portfolio.blog.domain.dto.postDto;

import com.portfolio.blog.domain.dto.user.User;
import com.portfolio.blog.domain.dto.category.CategoryResponse;
import com.portfolio.blog.domain.dto.tag.TagResponse;
import com.portfolio.blog.domain.entities.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    String title;
    LocalDateTime created;
    User user;
    StatusEntity status;
    Integer readingTime;
    CategoryResponse categories;
    Set<TagResponse> tags;
}
