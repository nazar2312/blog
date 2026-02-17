package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.category.CategoryResponse;
import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.entities.CategoryEntity;
import com.portfolio.blog.mappers.CategoryMapper;
import com.portfolio.blog.repositories.CategoryRepository;
import com.portfolio.blog.services.CategoryServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoryService implements CategoryServiceInterface {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    public CategoryService(CategoryRepository repo, CategoryMapper mapper) {
        this.repository = repo;
        this.mapper = mapper;
    }

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
        
        if(category.isEmpty()) throw new EntityNotFoundException();
        
        return mapper.entityToResponse(category.get());
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {

        CategoryEntity entity = mapper.requestToEntity(request);

        return mapper.entityToResponse(repository.save(entity));

    }

    @Override
    public void deleteCategory(UUID toDelete) {

        repository.deleteById(toDelete);
    }

}
