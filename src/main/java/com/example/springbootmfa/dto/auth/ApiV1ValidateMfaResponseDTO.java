package com.example.springbootmfa.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for MFA validation response")
public class ApiV1ValidateMfaResponseDTO {
    
    @Schema(description = "Indicates if the MFA code is valid", example = "true")
    private boolean valid;
}