package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.category.CategoryResponse;
import com.portfolio.blog.services.impl.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/categories")
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listCategories() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.findAllCategories());

    }

    @GetMapping(path = "/{category_id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable UUID category_id) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.findCategoryById(category_id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody CategoryRequest category) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createCategory(category));
    }

    @DeleteMapping(path = "/{category_id}")
    public ResponseEntity<Void> deleteCategory( @PathVariable UUID category_id) {

        service.deleteCategory(category_id);
        return ResponseEntity.noContent().build();
    }

}
