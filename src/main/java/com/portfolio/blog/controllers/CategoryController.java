package com.portfolio.blog.controllers;

import com.portfolio.blog.domain.dto.Category;
import com.portfolio.blog.services.impl.CategoryService;
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
    public ResponseEntity<List<Category>> listCategories() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.findAllCategories());

    }

    @GetMapping(path = "/api/{category_id}")
    public ResponseEntity<Category> getCategory(@PathVariable UUID category_id) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(service.findCategoryById(category_id));
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.createCategory(category));
    }

    @PatchMapping(path = "/api/{category_id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable UUID category_id,
            @RequestBody Category category) {

        return ResponseEntity.ok(
                service.updateCategory(category_id, category)
        );
    }

    @DeleteMapping(path = "/api/{category_id}/")
    public ResponseEntity<Category> deleteCategory(@PathVariable UUID category_id) {

        service.deleteCategory(category_id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
