package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Set;

@Schema(description = "Detailed Data Transfer Object for a single user, including their roles and permissions")
public record UserDetailDto(
        @Schema(description = "The unique identifier of the user") Long id,
        @Schema(description = "The username of the user") String username,
        @Schema(description = "Timestamp when the user was created") Instant createdAt,
        @Schema(description = "Timestamp when the user was last updated") Instant updatedAt,
        @Schema(description = "Set of roles assigned to the user, including their permissions") Set<RoleDto> roles
) {
}