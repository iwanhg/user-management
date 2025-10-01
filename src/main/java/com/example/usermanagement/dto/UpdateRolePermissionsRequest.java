package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

@Schema(description = "Request payload to update the permissions for a role")
public record UpdateRolePermissionsRequest(
        @NotNull
        @Schema(description = "A set of permission names to assign to the role") Set<String> permissionNames
) {}