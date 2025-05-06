package com.example.springbootmfa.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO for creating a new user")
public class ApiV1CreateUserRequestDTO {
    
    @NotBlank(message = "Name is required")
    @Schema(description = "User's name", example = "John Doe")
    private String name;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "strongPassword123")
    private String password;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email", example = "john.doe@example.com")
    private String email;
}