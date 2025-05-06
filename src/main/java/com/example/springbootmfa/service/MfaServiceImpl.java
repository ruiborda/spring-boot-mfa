package com.example.springbootmfa.service;

import com.example.springbootmfa.repository.UserRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MfaServiceImpl implements MfaService {

    private final UserRepository userRepository;
    private static final String ISSUER = "Spring Boot MFA App";

    @Override
    public String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }

    @Override
    public String generateQrCodeImageBase64(String email, String secretKey) {
        try {
            String barCodeData = getGoogleAuthenticatorBarCode(secretKey, email, ISSUER);
            BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, 200, 200);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
            
            String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + base64Image;
        } catch (WriterException | IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating QR code");
        }
    }

    @Override
    public boolean validateCode(String secretKey, String code) {
        if (secretKey == null || code == null) {
            return false;
        }
        String expectedCode = getTOTPCode(secretKey);
        return expectedCode.equals(code);
    }

    @Override
    public boolean enableMfa(UUID userId) {
        // Este método ya no se usa directamente
        throw new UnsupportedOperationException("Use AuthService.enableMfa instead");
    }

    @Override
    public boolean validateLogin(String email, String code) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        if (!user.getMfaEnabled() || user.getMfaSecret() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "MFA not enabled for this user");
        }
        
        return validateCode(user.getMfaSecret(), code);
    }

    private String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    private String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, StandardCharsets.UTF_8.toString()).replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8.toString()).replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating barcode URL");
        }
    }
}