package com.example.springbootmfa.controller;

import com.example.springbootmfa.dto.auth.ApiV1LoginRequestDTO;
import com.example.springbootmfa.dto.auth.ApiV1ValidateMfaRequestDTO;
import com.example.springbootmfa.service.AuthService;
import com.example.springbootmfa.service.MfaService;
import com.example.springbootmfa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebController {

    private final AuthService authService;
    private final UserService userService;
    private final MfaService mfaService;

    @GetMapping("/")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequest", new ApiV1LoginRequestDTO());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute ApiV1LoginRequestDTO loginRequest, Model model) {
        try {
            var response = authService.login(loginRequest);
            if (response.isRequiresMfa()) {
                model.addAttribute("email", loginRequest.getEmail());
                model.addAttribute("userId", response.getUserId());
                return "validate-mfa";
            }
            return "redirect:/dashboard?userId=" + response.getUserId();
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam UUID userId, Model model) {
        try {
            var user = userService.getUserById(userId);
            model.addAttribute("user", user);
            return "dashboard";
        } catch (Exception e) {
            return "redirect:/?error=session-expired";
        }
    }

    @GetMapping("/setup-mfa")
    public String setupMfa(@RequestParam UUID userId, Model model) {
        try {
            var user = userService.getUserById(userId);
            var response = authService.enableMfa(userId);
            model.addAttribute("qrCode", response.getQrCode());
            model.addAttribute("secret", response.getSecret());
            model.addAttribute("user", user);
            return "setup-mfa";
        } catch (Exception e) {
            log.error("Error setting up MFA", e);
            return "redirect:/dashboard?error=mfa-setup-failed&userId=" + userId;
        }
    }

    @PostMapping("/validate-mfa")
    public String validateMfa(@RequestParam String email, 
                            @RequestParam String code, 
                            @RequestParam UUID userId,
                            @RequestParam(required = false) String secretKey,
                            Model model) {
        try {
            var validateRequest = new ApiV1ValidateMfaRequestDTO();
            validateRequest.setEmail(email);
            validateRequest.setCode(code);
            validateRequest.setSecretKey(secretKey);
            
            // Si hay secretKey, estamos en el proceso de setup
            if (secretKey != null && !secretKey.isEmpty()) {
                authService.validateMfa(validateRequest);
            } else {
                // Si no hay secretKey, es una validación normal de login
                if (!mfaService.validateLogin(email, code)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid MFA code");
                }
            }
            
            return "redirect:/dashboard?userId=" + userId;
        } catch (Exception e) {
            log.error("MFA validation error", e);
            model.addAttribute("error", "Invalid MFA code");
            model.addAttribute("email", email);
            model.addAttribute("userId", userId);
            model.addAttribute("secretKey", secretKey);
            // Si tenemos secretKey, volvemos a la página de setup
            if (secretKey != null && !secretKey.isEmpty()) {
                var user = userService.getUserById(userId);
                var response = authService.enableMfa(userId);
                model.addAttribute("qrCode", response.getQrCode());
                model.addAttribute("secret", response.getSecret());
                model.addAttribute("user", user);
                return "setup-mfa";
            }
            return "validate-mfa";
        }
    }
}