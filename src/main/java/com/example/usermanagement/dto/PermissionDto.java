package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for a permission")
public record PermissionDto(
        @Schema(description = "Unique identifier of the permission") Integer id,
        @Schema(description = "Name of the permission (e.g., 'CREATE_USER')") String name
) {}