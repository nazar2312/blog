package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.category.CategoryResponse;
import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.entities.CategoryEntity;

import java.util.List;
import java.util.UUID;

public interface CategoryServiceInterface {

     List<CategoryResponse> findAllCategories();
     CategoryResponse findCategoryById(UUID id);
     CategoryResponse findCategoryByName(String name);
     CategoryResponse createCategory(CategoryRequest category);
     void deleteCategory(UUID toDelete);
     CategoryEntity verifyCategory(PostRequest request);
}
