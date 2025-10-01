package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request payload for creating a new role")
public record CreateRoleRequest(
        @NotBlank @Pattern(regexp = "^ROLE_[A-Z_]+$", message = "Role name must start with 'ROLE_' and contain only uppercase letters and underscores")
        @Schema(description = "Name of the new role. Must start with 'ROLE_'", example = "ROLE_WAREHOUSE_MANAGER") String name
) {}