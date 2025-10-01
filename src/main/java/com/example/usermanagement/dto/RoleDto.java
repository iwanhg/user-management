package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Data Transfer Object for a role, including its permissions")
public record RoleDto(
        @Schema(description = "Unique identifier of the role") Integer id,
        @Schema(description = "Name of the role (e.g., 'ROLE_ADMIN')") String name,
        @Schema(description = "Set of permissions associated with this role") Set<PermissionDto> permissions
) {}