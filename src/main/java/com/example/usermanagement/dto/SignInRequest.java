package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request payload for user sign-in")
public record SignInRequest(
        @NotBlank(message = "Username cannot be blank")
        @Schema(description = "User's username", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
        String username,
        @NotBlank(message = "Password cannot be blank")
        @Schema(description = "User's password", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {}