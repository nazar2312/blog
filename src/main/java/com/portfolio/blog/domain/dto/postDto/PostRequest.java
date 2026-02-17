package com.portfolio.blog.domain.dto.postDto;

import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.tag.TagRequest;
import com.portfolio.blog.domain.entities.StatusEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    String title;
    String content;
    StatusEntity status;
    CategoryRequest categories;
    Set<TagRequest> tags;
}
