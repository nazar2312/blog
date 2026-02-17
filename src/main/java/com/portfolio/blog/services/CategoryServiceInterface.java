package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.category.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryServiceInterface {

     List<CategoryResponse> findAllCategories();
     CategoryResponse findCategoryById(UUID id);
     CategoryResponse createCategory(CategoryRequest category);
     void deleteCategory(UUID toDelete);

}
