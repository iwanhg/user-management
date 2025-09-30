package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

@Schema(description = "Request payload for updating an existing user. All fields are optional.")
public record UpdateUserRequest(
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        @Schema(description = "New username for the user", example = "john.doe.new")
        String username,
        @Size(min = 8, message = "Password must be at least 8 characters long")
        @Schema(description = "New password for the user (at least 8 characters)", example = "newPassword456")
        String password
) {}