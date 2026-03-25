package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.mappers.PostMapper;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.*;
import com.portfolio.blog.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService implements PostServiceInterface {

    private final PostRepository repository;
    private final PostMapper mapper;
    private final UserServiceInterface userService;
    private final CategoryServiceInterface categoryService;
    private final TagServiceInterface tagService;
    private final AuthorizationServiceInterface authorizationService;

    @Override
    @Transactional
    public List<PostResponse> findAll() {

        List<PostEntity> posts = repository.findAll();

        return posts.stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional
    public PostResponse findOne(UUID id) {

        PostEntity post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post is not found "));
        return mapper.entityToResponse(post);
    }

    @Override
    @Transactional
    public PostResponse create(PostRequest request) {

        PostEntity postEntity = mapper.requestToEntity(request);

        postEntity.setAuthor(userService.extractUserFromSecurityContextHolder());
        postEntity.setCategory(categoryService.verifyCategory(request));
        postEntity.setTags(tagService.verifyTags(request));

        return mapper.entityToResponse(
                repository.save(postEntity)
        );
    }


    @Override
    @Transactional
    public PostResponse update(UUID uuid, PostRequest request) {

        PostEntity postToUpdate = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Post is not found "));

        UserEntity currentUser = userService.extractUserFromSecurityContextHolder();

        authorizationService.authorizeUpdating(postToUpdate, currentUser); //Exception is thrown if not authorized;

        postToUpdate.setTitle(request.getTitle());
        postToUpdate.setContent(request.getContent());
        postToUpdate.setStatus(request.getStatus());
        postToUpdate.setCategory(
                categoryService.verifyCategory(request)
        );
        postToUpdate.setTags(
                tagService.verifyTags(request)
        );

        return mapper.entityToResponse(
                repository.save(postToUpdate)
        );
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {

        PostEntity postToDelete = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Post is not found"));

        UserEntity currentUser = userService.extractUserFromSecurityContextHolder();

        authorizationService.authorizeDeleting(postToDelete, currentUser); //Exception is thrown if unauthorized;
        repository.deleteById(uuid);

        log.info("User [ {} ] has deleted post: {}", currentUser.getEmail(), postToDelete.getTitle());

    }
}














