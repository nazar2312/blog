package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.mappers.PostMapper;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
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

        if (posts.isEmpty()) throw new EntityNotFoundException("Posts are not found");

        return posts.stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    @Override
    @Transactional
    public PostResponse findOne(UUID id) {

        PostEntity post = repository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.entityToResponse(post);
    }

    @Override
    @Transactional
    public PostResponse create(PostRequest request) {

        PostEntity postEntity = mapper.requestToEntity(request);

        postEntity.setAuthor(userService.extractUserFromSecurityContextHolder());
        postEntity.setReadingTime(
                calculateReadingTime(request.getContent())
        );
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
                .orElseThrow(EntityNotFoundException::new);
        UserEntity currentUser = userService.extractUserFromSecurityContextHolder();

        try {

            authorizationService.authorizeUpdating(postToUpdate, currentUser);

            postToUpdate.setTitle(request.getTitle());
            postToUpdate.setContent(request.getContent());
            postToUpdate.setStatus(request.getStatus());
            postToUpdate.setCategory(
                    categoryService.verifyCategory(request)
            );
            postToUpdate.setTags(
                    tagService.verifyTags(request)
            );
            postToUpdate.setReadingTime(
                    calculateReadingTime(request.getContent())
            );

        } catch (AuthorizationServiceException e) {
            throw new AccessDeniedException("");
        }

        return mapper.entityToResponse(
                repository.save(postToUpdate)
        );
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {

        PostEntity postToDelete = repository.findById(uuid)
                .orElseThrow(EntityNotFoundException::new);
        UserEntity currentUser = userService.extractUserFromSecurityContextHolder();

        try {

            authorizationService.authorizeDeleting(postToDelete, currentUser);
            repository.deleteById(uuid);
            log.info("{} has deleted post: {}", currentUser.getUsername(), postToDelete.getTitle());

        } catch (AuthorizationServiceException e) {
            throw new AccessDeniedException("");
        }

    }

    private int calculateReadingTime(String content) {

        int wordsInTheContent = content.trim().split("\\s+").length;
        int averagePerSecond = 3;

        // return 60 seconds if less than 200 words;
        if (wordsInTheContent < 200) return 60;

        return wordsInTheContent / averagePerSecond;
    }
}














