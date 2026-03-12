package com.portfolio.blog.domain.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    DTO that represents authentication request;
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @Email(message = "Valid email must be provided")
    @NotBlank(message = "Email must be provided")
    private String email;
    @NotBlank(message = "Password must be provided")
    @Size(max = 100, message = "Password is too long")
    private String password;
}
