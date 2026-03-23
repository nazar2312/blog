package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.category.CategoryResponse;
import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.post.PostRequest;
import com.portfolio.blog.domain.entities.CategoryEntity;
import com.portfolio.blog.mappers.CategoryMapper;
import com.portfolio.blog.repositories.CategoryRepository;
import com.portfolio.blog.services.CategoryServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;
    private final AuthorizationService authorizationService;

    @Override
    public List<CategoryResponse> findAllCategories() {

        List<CategoryEntity> categories = repository.findAll();

        if(categories.isEmpty()) throw new EntityNotFoundException();

        return categories.stream()
                .map(mapper::entityToResponse)
                .toList();
    }

    @Override
    public CategoryResponse findCategoryById(UUID id) {

        Optional<CategoryEntity> category = repository.findById(id);
        
        if(category.isEmpty()) throw new EntityNotFoundException("Only existing categories can be viewed");
        
        return mapper.entityToResponse(category.get());
    }

    @Override
    public CategoryResponse findCategoryByName(String name) {

        Optional<CategoryEntity> category = repository.findByName(name);

        if(category.isEmpty()) throw new EntityNotFoundException("Only existing categories can be viewed");

        return mapper.entityToResponse(category.get());
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        CategoryEntity entity = mapper.requestToEntity(request);

        return mapper.entityToResponse(repository.save(entity));

    }

    @Override
    public void deleteCategory(UUID toDelete) {

        try {
            authorizationService.authorizeCategoryOrTagDeleting();
            repository.deleteById(toDelete);
        } catch (AuthorizationServiceException e) {
            throw new AccessDeniedException("");
        }
    }

    @Override
    public CategoryEntity verifyCategory(PostRequest request) {

        //  Method is only used by postService while creating new post.
        String name = request.getCategory().getName();
        if(name == null || name.isBlank()) throw new IllegalArgumentException("Please enter category name");

        Optional<CategoryEntity> category = repository.findByName(name);
        if(category.isEmpty()) throw new EntityNotFoundException("Only existing category can be assigned to the post");

        return category.get();
    }

}
