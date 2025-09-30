package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Data Transfer Object for user details (password is not exposed)")
public record UserDto(
        @Schema(description = "The unique identifier of the user", example = "1")
        Long id,
        @Schema(description = "The username of the user", example = "john.doe")
        String username,
        @Schema(description = "Timestamp when the user was created")
        Instant createdAt,
        @Schema(description = "Timestamp when the user was last updated")
        Instant updatedAt
) {
}