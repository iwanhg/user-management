package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for creating a new user")
public record CreateUserRequest(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "User's unique username", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Schema(description = "User's password (at least 8 characters)", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}