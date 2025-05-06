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
@Schema(description = "DTO for enable MFA response")
public class ApiV1EnableMfaResponseDTO {
    
    @Schema(description = "Base64 encoded QR code image with data URL prefix", example = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...")
    private String qrCode;
    
    @Schema(description = "Secret key for MFA", example = "JBSWY3DPEHPK3PXP")
    private String secret;
}