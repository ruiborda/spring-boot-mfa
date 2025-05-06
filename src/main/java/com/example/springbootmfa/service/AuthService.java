package com.example.springbootmfa.service;

import com.example.springbootmfa.dto.auth.ApiV1LoginRequestDTO;
import com.example.springbootmfa.dto.auth.ApiV1LoginResponseDTO;
import com.example.springbootmfa.dto.auth.ApiV1EnableMfaResponseDTO;
import com.example.springbootmfa.dto.auth.ApiV1ValidateMfaRequestDTO;

import java.util.UUID;

public interface AuthService {
    ApiV1LoginResponseDTO login(ApiV1LoginRequestDTO loginRequest);
    ApiV1EnableMfaResponseDTO enableMfa(UUID userId);
    void validateMfa(ApiV1ValidateMfaRequestDTO validateRequest);
}