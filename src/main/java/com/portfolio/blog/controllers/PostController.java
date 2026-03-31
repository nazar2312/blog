package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.services.PostServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/*
   GET/posts/ - Get all posts without specifying user
   GET/{user_id}/posts - posts of specific user
   GET/{post_id}/ - post with specific ID
   GET/{category_id}/posts - posts of specific category
   GET/{user_id}/{category_id}/posts - posts of specific user and specific category
   GET/{tag_id}/posts - posts with specific tag

   POST/posts - create post

   PATCH/{post_id} - update post

   DELETE/{post_id} - delete post

 */
@RestController
@RequestMapping(path = "api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceInterface service;

    @GetMapping
    public ResponseEntity<List<PostResponse>> findAll() {
        return ResponseEntity.ok().body(service.findAll());
    }

    @GetMapping(path = "/{post_id}")
    public ResponseEntity<PostResponse> findOne(@PathVariable UUID post_id) {
        return ResponseEntity.ok(service.findOne(post_id));
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(@Valid @RequestBody PostRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @PutMapping("/{post_id}")
    public ResponseEntity<PostResponse> update(
            @PathVariable UUID post_id,
            @Valid @RequestBody PostRequest postRequest
    ) {
        return ResponseEntity.ok()
                .body(service.update(post_id, postRequest));
    }

    @DeleteMapping(path = "/{post_id}")
    public ResponseEntity<PostResponse> delete(@PathVariable UUID post_id) {

        service.delete(post_id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
















