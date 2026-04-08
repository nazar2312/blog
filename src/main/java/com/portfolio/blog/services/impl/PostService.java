package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.domain.entities.Role;
import com.portfolio.blog.domain.entities.StatusEntity;
import com.portfolio.blog.domain.entities.UserEntity;
import com.portfolio.blog.mappers.PostMapper;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.*;
import com.portfolio.blog.exceptions.ResourceNotFoundException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
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
    public PostResponse findOne(UUID postToFind) {

        // Anonym (null) only can see with status PUBLISHED; USER can see PUBLISHED and his DRAFT; ADMIN - no restrictions;
        UserEntity currentUser = userService.getUserFromSecurityContextHolder();
        Specification<PostEntity> spec = Specification.allOf();

        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("id"), postToFind));

        if (currentUser == null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), StatusEntity.PUBLISHED));
        } else if (currentUser.getRole() == Role.USER) {

            spec = spec.and((root, query, criteriaBuilder) ->

                    // Either post is published or it's users DRAFT
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("status"), StatusEntity.PUBLISHED),
                            criteriaBuilder.equal(root.get("author").get("id"), currentUser.getId())
                    )
            );
        }
        return mapper.entityToResponse(
                repository.findOne(spec)
                        .orElseThrow(() -> new ResourceNotFoundException("Post is not found"))
        );
    }

    @Override
    @Transactional
    public List<PostResponse> findSpecific(UUID authorId,
                                           StatusEntity status,
                                           String categoryName,
                                           int pageNumber,
                                           int size
    ) {
        // 3 cases: ADMIN, USER, null(anonym)
        UserEntity currentUser = userService.getUserFromSecurityContextHolder();
        PageRequest page = PageRequest.of(pageNumber, size);
        Specification<PostEntity> spec = Specification.allOf();

        if (authorId != null)
            spec = spec.and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("author").get("id"), authorId)));
        if (status != null)
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        if (categoryName != null)
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category").get("name"), categoryName));

        /*
            Anonymous users can only see published posts,
            USER can see PUBLISHED and his own DRAFT,
            ADMIN has no restrictions.
        */
        if (currentUser == null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), StatusEntity.PUBLISHED));

        } else if (currentUser.getRole().equals(Role.USER)) {

            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.equal(root.get("status"), StatusEntity.PUBLISHED),
                            criteriaBuilder.equal(root.get("author"), currentUser)
                    )
            );
        }

        // Pulling all SPECIFIED posts, and store their uuids
        var uuids = repository.findAll(spec, page).stream()
                .map(PostEntity::getId)
                .toList();

        // Finding specified posts and fetching related data;
        return repository.findByIdIn(uuids).stream()
                .map(mapper::entityToResponse)
                .toList();
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

}














