package com.example.springbootmfa.controller;

import com.example.springbootmfa.dto.user.ApiV1CreateUserRequestDTO;
import com.example.springbootmfa.dto.user.ApiV1UserResponseDTO;
import com.example.springbootmfa.mappers.UserMapper;
import com.example.springbootmfa.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management endpoints")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    @Operation(summary = "Create a new user", description = "Creates a new user with the provided information")
    @ApiResponse(responseCode = "201", description = "User created successfully")
    public ResponseEntity<ApiV1UserResponseDTO> createUser(
            @Valid @RequestBody ApiV1CreateUserRequestDTO createUserDTO) {
        var user = userMapper.toEntity(createUserDTO);
        var createdUser = userService.createUser(user);
        return new ResponseEntity<>(userMapper.toDto(createdUser), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves a list of all users")
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    public ResponseEntity<List<ApiV1UserResponseDTO>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(userMapper.toDto(users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their UUID")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<ApiV1UserResponseDTO> getUserById(
            @Parameter(description = "User UUID") @PathVariable UUID id) {
        var user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Updates an existing user's information")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<ApiV1UserResponseDTO> updateUser(
            @Parameter(description = "User UUID") @PathVariable UUID id,
            @Valid @RequestBody ApiV1CreateUserRequestDTO updateUserDTO) {
        var user = userMapper.toEntity(updateUserDTO);
        var updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(userMapper.toDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Deletes a user by their UUID")
    @ApiResponse(responseCode = "204", description = "User deleted successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User UUID") @PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}