package com.example.springbootmfa.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.UUID;

@Data
@Schema(description = "DTO for user response")
public class ApiV1UserResponseDTO {
    
    @Schema(description = "User's unique identifier")
    private UUID id;
    
    @Schema(description = "User's name")
    private String name;
    
    @Schema(description = "User's email")
    private String email;
    
    @Schema(description = "Whether MFA is enabled for the user")
    private Boolean mfaEnabled;
    
    @Schema(description = "MFA secret for Google Authenticator (only shown during setup)")
    private String mfaSecret;
}