package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.tag.TagRequest;
import com.portfolio.blog.domain.dto.tag.TagResponse;
import com.portfolio.blog.domain.entities.TagEntity;

import java.util.Set;
import java.util.UUID;

public interface TagServiceInterface {
    TagResponse create(TagRequest request);
    Set<TagResponse> findAll();
    void deleteById(UUID uuid);
    Set<TagEntity> verifyTags(PostRequest request);
}
