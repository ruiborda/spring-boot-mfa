package com.example.springbootmfa.service;

import java.util.UUID;

public interface MfaService {
    String generateSecretKey();
    String generateQrCodeImageBase64(String email, String secretKey);
    boolean validateCode(String secretKey, String code);
    boolean enableMfa(UUID userId);
    boolean validateLogin(String email, String code);
}