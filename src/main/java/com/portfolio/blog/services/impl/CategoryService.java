package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.category.CategoryResponse;
import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.entities.CategoryEntity;
import com.portfolio.blog.exceptions.ResourceNotFoundException;
import com.portfolio.blog.mappers.CategoryMapper;
import com.portfolio.blog.repositories.CategoryRepository;
import com.portfolio.blog.services.AuthorizationServiceInterface;
import com.portfolio.blog.services.CategoryServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;
    private final AuthorizationServiceInterface authorizationService;

    @Override
    public List<CategoryResponse> findAllCategories() {

        List<CategoryEntity> categories = repository.findAll();

        return categories.stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    @Override
    public CategoryResponse findCategoryById(UUID id) {

        CategoryEntity category = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category is not found with such id: " + id));

        return mapper.entityToResponse(category);
    }

    @Override
    public CategoryResponse findCategoryByName(String name) {

        CategoryEntity category = repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category with name: " + name + " is not found"));


        return mapper.entityToResponse(category);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        CategoryEntity entity = mapper.requestToEntity(request);

        return mapper.entityToResponse(repository.save(entity));
    }

    @Override
    public void deleteCategory(UUID toDelete) {

        authorizationService.authorizeCategoryOrTagDeleting(); // Forbidden exception is thrown in case if unauthorized;
        repository.deleteById(toDelete);
    }


    @Override //  Method is only used by postService when creating new post.
    public CategoryEntity verifyCategory(PostRequest request) {

        String name = request.getCategory().getName();

        return repository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Only existing category can be assigned to the post"));
    }

}
