package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "Detailed Data Transfer Object for a single role, including its permissions and assigned users")
public record RoleDetailDto(
        @Schema(description = "Unique identifier of the role") Integer id,
        @Schema(description = "Name of the role") String name,
        @Schema(description = "Set of permissions associated with this role") Set<PermissionDto> permissions,
        @Schema(description = "Set of users assigned to this role") Set<UserDto> users
) {}