package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response payload containing the JWT access token")
public record SignInResponse(
        @Schema(description = "JWT token for authenticating subsequent requests", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token,

        @Schema(description = "The type of token", example = "Bearer")
        String authType,

        @Schema(description = "The duration of the token's validity in milliseconds", example = "3600000")
        long expiresIn,

        @Schema(description = "The refresh token to be used to obtain a new access token")
        String refreshToken
) {}