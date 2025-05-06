package com.example.springbootmfa.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO for MFA code validation request")
public class ApiV1ValidateMfaRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "User's email", example = "john.doe@example.com")
    private String email;
    
    @NotBlank(message = "Code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Code must be 6 digits")
    @Schema(description = "6-digit MFA code", example = "123456")
    private String code;
    
    @NotBlank(message = "Secret key is required")
    @Schema(description = "Temporary MFA secret key for validation", example = "JBSWY3DPEHPK3PXP")
    private String secretKey;
}