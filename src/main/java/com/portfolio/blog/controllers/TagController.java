package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.tag.TagRequest;
import com.portfolio.blog.domain.dto.tag.TagResponse;
import com.portfolio.blog.services.TagServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagServiceInterface service;

    @GetMapping
    public ResponseEntity<List<TagResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @PostMapping
    public ResponseEntity<TagResponse> create(@Valid @RequestBody TagRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @DeleteMapping(path = "/{uuid}")
    public void delete(@PathVariable UUID uuid) {
        service.deleteById(uuid);
    }
}
