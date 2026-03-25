package com.portfolio.blog.domain.dto.post;

import com.portfolio.blog.domain.dto.category.CategoryRequest;
import com.portfolio.blog.domain.dto.tag.TagRequest;
import com.portfolio.blog.domain.entities.StatusEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {

    @NotBlank(message = "Title must contain at least two letters")
    @Size(min = 2, max = 50, message = "Title must be the size of 2 to 50 letters")
    @Pattern(regexp = "^[a-zA-Z ]+$", message = "Title must contain only letters")
    String title;

    @NotBlank(message = "Content must be provided")
    @Size(min = 1, max = 10000, message = "Content size must be in a range from 1 to 10000 characters")
    String content;

    @NotNull(message = "Status must be specified (PUBLISHED/DRAFT)")
    StatusEntity status;

    @NotNull(message = "Category must be specified")
    @Valid
    CategoryRequest category;

    @NotEmpty(message = "Tags must be provided")
    @Size(max = 20)
    @Valid
    List<TagRequest> tags;
}
