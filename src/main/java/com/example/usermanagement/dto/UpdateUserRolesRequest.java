package com.example.usermanagement.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

@Schema(description = "Request payload to update the roles of a user")
public record UpdateUserRolesRequest(
        @NotEmpty
        @Schema(description = "A set of role names to assign to the user (e.g., [\"ROLE_USER\", \"ROLE_WAREHOUSE_MANAGER\"])",
                example = "[\"ROLE_USER\"]")
        Set<String> roleNames
) {}