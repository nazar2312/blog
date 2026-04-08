package com.portfolio.blog.domain.dto.tag;


import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagRequest {
    @Size(min = 1, max = 30, message = "Tag name size must be from 1 to 30 characters")
    String name;
}
