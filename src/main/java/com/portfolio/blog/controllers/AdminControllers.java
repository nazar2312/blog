package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.services.PostServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin")
public class AdminControllers {

    private final PostServiceInterface postService;

    @PostMapping(path = "/delete_post/{id}")
    public ResponseEntity<PostResponse> deletePost(@PathVariable UUID id) {

    postService.delete(id);

    return ResponseEntity.noContent().build();
}

}
