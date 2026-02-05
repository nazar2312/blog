package com.portfolio.blog.services;

import com.portfolio.blog.domain.dto.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryServiceInterface {

     List<Category> findAllCategories();
     Category findCategoryById(UUID id);
     Category createCategory(Category category);
     Category updateCategory(UUID id, Category toUpdate);
     void deleteCategory(UUID toDelete);

}
