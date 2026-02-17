package com.portfolio.blog.mappers;

import com.portfolio.blog.domain.dto.tag.TagRequest;
import com.portfolio.blog.domain.entities.TagEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    TagEntity requestToEntity(TagRequest tag);
}
