package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.StatusEntity;

import java.util.List;
import java.util.UUID;

public interface PostServiceInterface {

    List<PostResponse> findSpecific(UUID authorId, StatusEntity status, String categoryName, int pageNumber, int size);
    PostResponse findOne(UUID id);

    PostResponse create(PostRequest request);
    PostResponse update(UUID uuid, PostRequest request);

    void delete(UUID uuid);
}
