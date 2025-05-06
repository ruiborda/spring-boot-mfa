package com.example.springbootmfa.controller;

import com.example.springbootmfa.dto.auth.*;
import com.example.springbootmfa.repository.UserRepository;
import com.example.springbootmfa.service.MfaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final MfaService mfaService;

    @PostMapping("/login")
    @Operation(summary = "Login with credentials", description = "Authenticate user with email and password")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<ApiV1LoginResponseDTO> login(@Valid @RequestBody ApiV1LoginRequestDTO loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        var result = BCrypt.verifyer().verify(
                loginRequest.getPassword().toCharArray(),
                user.getPasswordHashed().toCharArray()
        );

        if (!result.verified) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return ResponseEntity.ok(ApiV1LoginResponseDTO.builder()
                .userId(user.getId())
                .requiresMfa(user.getMfaEnabled())
                .build());
    }

    @PostMapping("/mfa/enable")
    @Operation(summary = "Enable MFA", description = "Enable MFA for a user and get QR code")
    @ApiResponse(responseCode = "200", description = "MFA enabled successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<ApiV1EnableMfaResponseDTO> enableMfa(@RequestParam UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String secretKey = mfaService.generateSecretKey();
        user.setMfaSecret(secretKey);
        user.setMfaEnabled(true);
        userRepository.save(user);

        String qrCodeImage = mfaService.generateQrCodeImageBase64(user.getEmail(), secretKey);
        return ResponseEntity.ok(ApiV1EnableMfaResponseDTO.builder()
                .qrCode(qrCodeImage)
                .secret(secretKey)
                .build());
    }

    @PostMapping("/mfa/validate")
    @Operation(summary = "Validate MFA code", description = "Validate a 6-digit MFA code")
    @ApiResponse(responseCode = "200", description = "Code validation successful")
    @ApiResponse(responseCode = "401", description = "Invalid code")
    public ResponseEntity<ApiV1ValidateMfaResponseDTO> validateMfa(@Valid @RequestBody ApiV1ValidateMfaRequestDTO validateRequest) {
        boolean isValid = mfaService.validateLogin(validateRequest.getEmail(), validateRequest.getCode());
        
        if (!isValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid MFA code");
        }

        return ResponseEntity.ok(ApiV1ValidateMfaResponseDTO.builder()
                .valid(true)
                .build());
    }
}