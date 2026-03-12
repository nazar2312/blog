package com.portfolio.blog.domain.dto.authentication;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Username must be provided")
    @Size(max = 20, message = "Username is too long")
    @Pattern(regexp = "^[\\w\\s-]+$", message = "Unauthorised characters were used when creating username")
    String username;

    @Email(message = "Valid email must be provided")
    @NotBlank(message = "Email must be provided")
    String email;

    @NotBlank(message = "Password must be provided")
    @Size(min = 8, max = 100, message = "Password size must be between 8 and 100 characters")
    String password;
}
