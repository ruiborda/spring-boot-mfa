package com.example.springbootmfa.service;

import com.example.springbootmfa.dto.auth.*;
import com.example.springbootmfa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final MfaService mfaService;

    @Override
    public ApiV1LoginResponseDTO login(ApiV1LoginRequestDTO loginRequest) {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        var result = BCrypt.verifyer().verify(
                loginRequest.getPassword().toCharArray(),
                user.getPasswordHashed().toCharArray()
        );

        if (!result.verified) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return ApiV1LoginResponseDTO.builder()
                .userId(user.getId())
                .requiresMfa(user.getMfaEnabled())
                .build();
    }

    @Override
    public ApiV1EnableMfaResponseDTO enableMfa(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Solo generamos el secreto temporalmente sin guardarlo aún
        String secretKey = mfaService.generateSecretKey();
        String qrCodeImage = mfaService.generateQrCodeImageBase64(user.getEmail(), secretKey);
        
        return ApiV1EnableMfaResponseDTO.builder()
                .qrCode(qrCodeImage)
                .secret(secretKey)
                .build();
    }

    @Override
    public void validateMfa(ApiV1ValidateMfaRequestDTO validateRequest) {
        var user = userRepository.findByEmail(validateRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (validateRequest.getSecretKey() != null) {
            // Validación durante la configuración inicial de MFA
            boolean isValid = mfaService.validateCode(validateRequest.getSecretKey(), validateRequest.getCode());
            
            if (!isValid) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid MFA code");
            }

            // Solo si la validación es exitosa, guardamos el secreto y activamos MFA
            user.setMfaSecret(validateRequest.getSecretKey());
            user.setMfaEnabled(true);
            userRepository.save(user);
        } else {
            // Validación normal de login MFA
            if (!user.getMfaEnabled() || user.getMfaSecret() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MFA not enabled for this user");
            }

            boolean isValid = mfaService.validateCode(user.getMfaSecret(), validateRequest.getCode());
            if (!isValid) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid MFA code");
            }
        }
    }
}