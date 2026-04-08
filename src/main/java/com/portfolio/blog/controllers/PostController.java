package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.entities.StatusEntity;
import com.portfolio.blog.services.PostServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostServiceInterface service;

    @GetMapping
    public ResponseEntity<List<PostResponse>> find(
            @RequestParam(required = false) UUID authorId,
            @RequestParam(required = false) StatusEntity status,
            @RequestParam(required = false) String categoryName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok().body(service.findSpecific(authorId, status, categoryName, page, size));
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
















