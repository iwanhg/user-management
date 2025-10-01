package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request payload for creating a new permission")
public record CreatePermissionRequest(
        @NotBlank @Pattern(regexp = "^[A-Z_]+$", message = "Permission name must contain only uppercase letters and underscores")
        @Schema(description = "Name of the new permission", example = "APPROVE_ORDER") String name
) {}