package com.example.springbootmfa.mappers;

import com.example.springbootmfa.dto.user.ApiV1CreateUserRequestDTO;
import com.example.springbootmfa.dto.user.ApiV1UserResponseDTO;
import com.example.springbootmfa.model.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    
    private static final int BCRYPT_COST = 12;

    public User toEntity(ApiV1CreateUserRequestDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswordHashed(BCrypt.withDefaults().hashToString(BCRYPT_COST, dto.getPassword().toCharArray()));
        user.setMfaEnabled(false);
        return user;
    }

    public ApiV1UserResponseDTO toDto(User user) {
        ApiV1UserResponseDTO dto = new ApiV1UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setMfaEnabled(user.getMfaEnabled());
        if (user.getMfaSecret() != null && !user.getMfaEnabled()) {
            dto.setMfaSecret(user.getMfaSecret());
        }
        return dto;
    }

    public List<ApiV1UserResponseDTO> toDto(List<User> users) {
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
