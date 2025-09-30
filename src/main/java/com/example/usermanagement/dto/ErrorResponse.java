package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Standard error response format")
public record ErrorResponse(
        @Schema(description = "Timestamp of when the error occurred", example = "2023-10-27T10:00:00")
        LocalDateTime timestamp,

        @Schema(description = "A brief, human-readable summary of the problem", example = "User not found")
        String message
) {
}