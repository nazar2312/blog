package com.portfolio.blog.services.impl;

import com.portfolio.blog.domain.dto.Category;
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
    public List<Category> findAllCategories() {

        List<CategoryEntity> categories = repository.findAll();

        if(categories.isEmpty()) throw new EntityNotFoundException();

        return categories.stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Category findCategoryById(UUID id) {

        Optional<CategoryEntity> category = repository.findById(id);
        
        if(category.isEmpty()) throw new EntityNotFoundException();
        
        return mapper.toDto(category.get());
    }

    @Override
    public Category createCategory(Category category) {
        
        if(category.id() != null) throw new IllegalArgumentException("ID must not be provided.");
        if(category.name() == null) throw new IllegalArgumentException("Name must be provided");

        return mapper.toDto(
                repository.save(mapper.toEntity(category))
        );
    }

    @Override
    public Category updateCategory(UUID id, Category category) {
        /*
            First, making sure that provided category match to the requirements;
            Then checking if category to update is actually persists in database;
            Update all fields and save to database;
         */
        if(category.id() != null) throw new IllegalArgumentException("ID must not be provided.");
        if(category.name() == null) throw new IllegalArgumentException("Name must be provided");

        Optional<CategoryEntity> toUpdate = repository.findById(id);
        if(toUpdate.isEmpty()) throw new EntityNotFoundException("Category does not exists, please provide different ID");

        toUpdate.get().setName(category.name());
        
        return mapper.toDto(
                repository.save(toUpdate.get())
        );
    }

    @Override
    public void deleteCategory(UUID toDelete) {

        repository.deleteById(toDelete);
    }

}
