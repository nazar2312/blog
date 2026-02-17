package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.postDto.PostRequest;
import com.portfolio.blog.domain.dto.postDto.PostResponse;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.mappers.PostMapper;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.PostServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements PostServiceInterface {

    private final PostRepository repository;
    private final PostMapper mapper;

    @Override
    public List<PostResponse> findAll() {

        List<PostResponse> posts = repository.findAll()
                .stream()
                .map(mapper::entityToResponse)
                .toList();

        if(posts.isEmpty()) throw new EntityNotFoundException("Posts are not found");

        return posts;
    }

    @Override
    public PostResponse findOne(UUID id) {

        Optional<PostEntity> post = repository.findById(id);

        if(post.isEmpty() ) throw new EntityNotFoundException("Post is not found");

        return mapper.entityToResponse(post.get());
    }

    @Override
    public PostResponse create(PostRequest request) {

        return null;
    }

    @Override
    public PostResponse update() {
        return null;
    }

    @Override
    public void delete() {

    }
}














