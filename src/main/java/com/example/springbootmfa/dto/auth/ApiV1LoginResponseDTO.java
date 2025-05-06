package com.example.springbootmfa.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for login response")
public class ApiV1LoginResponseDTO {
    
    @Schema(description = "User's unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;
    
    @Schema(description = "Indicates if MFA validation is required", example = "true")
    private boolean requiresMfa;
}