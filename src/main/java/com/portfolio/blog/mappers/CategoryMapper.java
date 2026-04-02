package com.portfolio.blog.mappers;

import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.category.CategoryResponse;
import com.portfolio.blog.domain.entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryResponse entityToResponse(CategoryEntity entity);

    CategoryEntity requestToEntity(CategoryRequest category);
}
