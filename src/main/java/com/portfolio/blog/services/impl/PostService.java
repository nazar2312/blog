package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.StatusEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.mappers.PostMapper;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.*;
import com.portfolio.blog.exceptions.ResourceNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final UserServiceInterface userService;


    @Override
    @Transactional
    public List<PostResponse> findAll(int pageNumber, int size) {

        /*
            Authenticated user can see all published and his drafts.
            If user is not authenticated (anonymous), return only published posts;
        */

        Pageable page = PageRequest.of(pageNumber, size);
        UserEntity currentUser = userService.getUserFromSecurityContextHolder();

        if (currentUser == null) {

            var paginated = repository.findByStatus(StatusEntity.PUBLISHED, page);

            if (!paginated.isEmpty()) {
                var uuids = paginated.stream()
                        .map(p -> p.getId())
                        .toList();
                return repository.findByIdIn(uuids).stream()
                        .map(mapper::entityToResponse)
                        .toList();

            } else return List.of(); //Return empty list if no elements found;

        } else { //If user is authenticated

            var paginated = repository.findByStatusOrAuthor(StatusEntity.PUBLISHED, currentUser, page);

            if(!paginated.isEmpty()) {

                var uuids = paginated.stream()
                        .map(p -> p.getId())
                        .toList();
                return repository.findByIdIn(uuids).stream()
                        .map(mapper::entityToResponse)
                        .toList();

            } else return List.of();
        }
    }


    @Override
    @Transactional
    public PostResponse findOne(UUID id) {

        UserEntity currentUser = userService.getUserFromSecurityContextHolder();

        PostEntity post = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post is not found with id [ " + id + " ]"));

        if (currentUser == null && post.getStatus().equals(StatusEntity.DRAFT)) {
            throw new ResourceNotFoundException("Post is unavailable/nonexisting");
        }

        if (post.getStatus().equals(StatusEntity.PUBLISHED) || post.getAuthor().getId().equals(currentUser.getId())) {
            return mapper.entityToResponse(post);
        } else {
            throw new ResourceNotFoundException("Post is unavailable/nonexisting");
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

        authorizationService.authorizeUpdatingOrDeleting(postToUpdate, currentUser); //Exception is thrown if not authorized;

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

        authorizationService.authorizeUpdatingOrDeleting(postToDelete, currentUser); //Exception is thrown if unauthorized;
        repository.deleteById(uuid);

        log.info("User [ {} ] has deleted post: {}", currentUser.getEmail(), postToDelete.getTitle());

    }

    @Override
    public List<PostResponse> findByAuthor(UUID author_id) {
        PageRequest pageSize = PageRequest.ofSize(2);
        return repository.findByStatusAndAuthorId(StatusEntity.PUBLISHED, author_id, pageSize).stream()
                .map(mapper::entityToResponse)
                .toList();
    }


}














