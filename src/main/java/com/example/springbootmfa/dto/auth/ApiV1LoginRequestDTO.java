package com.example.springbootmfa.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "DTO for login request")
public class ApiV1LoginRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email", example = "john.doe@example.com")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Schema(description = "User's password", example = "strongPassword123")
    private String password;
}