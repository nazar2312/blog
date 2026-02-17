package com.portfolio.blog.domain.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 20, message = "Category name size must be between 2 and 20 characters")
    @Pattern(regexp = "^[\\w\\s-]+$", message = "Unauthorised characters were used when creating name")
    private String name;
}
