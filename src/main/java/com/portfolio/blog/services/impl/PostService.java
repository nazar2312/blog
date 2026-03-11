package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.PostEntity;
import com.portfolio.blog.mappers.PostMapper;
import com.portfolio.blog.repositories.PostRepository;
import com.portfolio.blog.services.CategoryServiceInterface;
import com.portfolio.blog.services.PostServiceInterface;
import com.portfolio.blog.services.TagServiceInterface;
import com.portfolio.blog.services.UserServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final UserServiceInterface userService;
    private final CategoryServiceInterface categoryService;
    private final TagServiceInterface tagService;

    @Override
    @Transactional
    public List<PostResponse> findAll() {

        List<PostResponse> posts = repository.findAll()
                .stream()
                .map(mapper::entityToResponse)
                .toList();

        if (posts.isEmpty()) throw new EntityNotFoundException("Posts are not found");

        return posts;
    }

    @Override
    public PostResponse findOne(UUID id) {

        Optional<PostEntity> post = repository.findById(id);

        if (post.isEmpty()) throw new EntityNotFoundException("Post is not found");

        return mapper.entityToResponse(post.get());
    }

    @Override
    public PostResponse create(PostRequest request) {

        PostEntity postEntity = mapper.requestToEntity(request);

        postEntity.setAuthor(userService.extractUserFromSecurityContextHolder());
        postEntity.setReadingTime(postEntity.getContent().length() / 2);
        postEntity.setCategory(categoryService.verifyCategory(request));
        postEntity.setTags(tagService.verifyTags(request));

        return mapper.entityToResponse(repository.save(postEntity));
    }


    @Override
    @Transactional
    public PostResponse update(UUID uuid, PostRequest request) {

        Optional<PostEntity> toUpdate = repository.findById(uuid);
        if (toUpdate.isEmpty())
            throw new EntityNotFoundException("Post with ID  " + uuid + " does not exist");
        PostEntity postEntity = toUpdate.get();

        postEntity.setTitle(request.getTitle());
        postEntity.setContent(request.getContent());
        postEntity.setStatus(request.getStatus());
        postEntity.setCategory(
                categoryService.verifyCategory(request)
        );
        postEntity.setTags(
                tagService.verifyTags(request)
        );

        return mapper.entityToResponse(
                repository.save(postEntity));
    }

    @Override
    public void delete(UUID uuid) {
        repository.deleteById(uuid);
    }

    private int calculateReadingTime(String content) {

        int wordsInTheContent = content.trim().split("\\s+").length;
        int averagePerSecond = 3;

        // return 60 seconds if less than 200 words;
        if(wordsInTheContent < 200) return 60;

        return wordsInTheContent / averagePerSecond;
    }
}














