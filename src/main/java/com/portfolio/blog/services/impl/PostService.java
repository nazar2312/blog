package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.StatusEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.exceptions.ForbiddenException;
import com.portfolio.blog.mappers.PostMapper;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.*;
import com.portfolio.blog.exceptions.ResourceNotFoundException;

import org.springframework.transaction.annotation.Transactional;
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
    private final CategoryServiceInterface categoryService;
    private final TagServiceInterface tagService;
    private final AuthorizationServiceInterface authorizationService;
    private final AuthenticationServiceInterface authenticationService;
    private final UserServiceInterface userService;

    @Override
    @Transactional
    public List<PostResponse> findAll() {

        UserEntity currentUser = userService.getUserFromSecurityContextHolder();

        return repository.findAll().stream()
                .filter(post -> post.getStatus().equals(StatusEntity.PUBLISHED) //Show all published posts
                        || (currentUser != null && post.getAuthor().getId().equals(currentUser.getId())))  // Show draft IF user is authenticated
                .map(mapper::entityToResponse).toList();
    }

    @Override
    @Transactional
    public PostResponse findOne(UUID id) {

        UserEntity currentUser = userService.getUserFromSecurityContextHolder();

        PostEntity post = repository.findById(id)
                .orElseThrow( () -> new ResourceNotFoundException("Post is not found with id [ " + id + " ]" ));

        if( post.getStatus().equals(StatusEntity.PUBLISHED) || post.getAuthor().getId().equals(currentUser.getId())) {
            return mapper.entityToResponse(post);
        } else {
            throw new ForbiddenException("No.");
        }
    }

    @Override
    @Transactional
    public PostResponse create(PostRequest request) {

        PostEntity postEntity = mapper.requestToEntity(request);
        postEntity.setAuthor(userService.getUserFromSecurityContextHolder());
        postEntity.setCategory(categoryService.verifyCategory(request));
        postEntity.setTags(tagService.verifyTags(request));

        return mapper.entityToResponse(repository.save(postEntity));
    }


    @Override
    @Transactional
    public PostResponse update(UUID uuid, PostRequest request) {

        PostEntity postToUpdate = repository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("Post is not found "));

        UserEntity currentUser = userService.getUserFromSecurityContextHolder();

        authorizationService.authorizeUpdating(postToUpdate, currentUser); //Exception is thrown if not authorized;

        postToUpdate.setTitle(request.getTitle());
        postToUpdate.setContent(request.getContent());
        postToUpdate.setStatus(request.getStatus());
        postToUpdate.setCategory(categoryService.verifyCategory(request));
        postToUpdate.setTags(tagService.verifyTags(request));

        return mapper.entityToResponse(repository.save(postToUpdate));
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {

        PostEntity postToDelete = repository.findById(uuid).orElseThrow(() -> new ResourceNotFoundException("Post is not found"));

        UserEntity currentUser = userService.getUserFromSecurityContextHolder();

        authorizationService.authorizeDeleting(postToDelete, currentUser); //Exception is thrown if unauthorized;
        repository.deleteById(uuid);

        log.info("User [ {} ] has deleted post: {}", currentUser.getEmail(), postToDelete.getTitle());

    }
}














