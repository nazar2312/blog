package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.post.PostResponse;
import com.portfolio.blog.domain.dto.user.User;
import com.portfolio.blog.services.AdministrationServiceInterface;
import com.portfolio.blog.services.PostServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/admin")
public class AdminController {

    private final AdministrationServiceInterface administrationServices;
    private final PostServiceInterface postService;

    @DeleteMapping(path = "/delete_post/{uuid}")
    public ResponseEntity<PostResponse> deletePost(@PathVariable UUID uuid) {

        postService.delete(uuid);

        return ResponseEntity.noContent().build();
    }
    @PostMapping(path = "/users/block-user/{uuid}")
    public ResponseEntity<String> blockUser(@PathVariable UUID uuid) {

        User blockedUser = administrationServices.block(uuid);

        return ResponseEntity.ok().body("User [" + blockedUser.email() +  " ] was permanently blocked" );
    }


}
