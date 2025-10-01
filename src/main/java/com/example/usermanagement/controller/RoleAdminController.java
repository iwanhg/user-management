package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CreateRoleRequest;
import com.example.usermanagement.dto.CreatePermissionRequest;
import com.example.usermanagement.dto.RoleDetailDto;
import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.dto.UpdateRolePermissionsRequest;
import com.example.usermanagement.service.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin: Role & Permission Management", description = "Endpoints for managing roles and permissions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('MANAGE_AUTHORIZATION')")
public class RoleAdminController {

    private final RolePermissionService rolePermissionService;

    @GetMapping("/roles")
    @Operation(summary = "Get all roles", description = "Retrieves a list of all roles and their assigned permissions.")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        return ResponseEntity.ok(rolePermissionService.getAllRoles());
    }

    @GetMapping("/roles/{id}/details")
    @Operation(summary = "Get detailed role view by ID", description = "Retrieves a single role by its ID, including all permissions and users assigned to it.")
    public ResponseEntity<RoleDetailDto> getRoleDetailsById(@PathVariable Integer id) {
        return ResponseEntity.ok(rolePermissionService.getRoleDetailsById(id));
    }

    @GetMapping("/permissions")
    @Operation(summary = "Get all permissions", description = "Retrieves a list of all available permissions in the system.")
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        return ResponseEntity.ok(rolePermissionService.getAllPermissions());
    }

    @PostMapping("/roles")
    @Operation(summary = "Create a new role", description = "Creates a new role with an empty set of permissions.")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody CreateRoleRequest request) {
        RoleDto createdRole = rolePermissionService.createRole(request);
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @PutMapping("/roles/{id}/permissions")
    @Operation(summary = "Update permissions for a role", description = "Sets the permissions for a specific role. This overwrites all existing permissions for the role.")
    public ResponseEntity<RoleDto> updateRolePermissions(@PathVariable Integer id, @Valid @RequestBody UpdateRolePermissionsRequest request) {
        return ResponseEntity.ok(rolePermissionService.updateRolePermissions(id, request));
    }

    @PostMapping("/permissions")
    @Operation(summary = "Create a new permission", description = "Creates a new permission that can be assigned to roles.")
    public ResponseEntity<PermissionDto> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        PermissionDto createdPermission = rolePermissionService.createPermission(request);
        return new ResponseEntity<>(createdPermission, HttpStatus.CREATED);
    }

    @DeleteMapping("/permissions/{id}")
    @Operation(summary = "Delete a permission", description = "Deletes a permission from the system. Fails if the permission is currently assigned to any role.")
    public ResponseEntity<Void> deletePermission(@PathVariable Integer id) {
        rolePermissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}