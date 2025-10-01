package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CreateUserRequest;
import com.example.usermanagement.dto.UpdateUserRolesRequest;
import com.example.usermanagement.dto.UpdateUserRequest;
import com.example.usermanagement.dto.UserDetailDto;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.dto.ErrorResponse;
import com.example.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for CRUD operations on users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    @Operation(summary = "Create a new user", description = "Adds a new user to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        UserDto createdUser = userService.createUser(createUserRequest);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('READ_USERS')")
    @Operation(summary = "Get all users (paginated)", description = "Retrieves a paginated list of all users.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    public ResponseEntity<Page<UserDto>> getAllUsers(@Parameter(hidden = true) Pageable pageable) {
        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_USERS')")
    @Operation(summary = "Get a user by ID", description = "Retrieves a single user by their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAuthority('READ_USERS')")
    @Operation(summary = "Get detailed user view by ID", description = "Retrieves a single user by their ID, including their assigned roles and permissions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDetailDto> getUserDetailsById(@PathVariable Long id) {
        UserDetailDto userDetails = userService.getUserDetailsById(id);
        return ResponseEntity.ok(userDetails);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    @Operation(summary = "Update an existing user", description = "Updates the details of an existing user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        UserDto updatedUser = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    @Operation(summary = "Delete a user by ID", description = "Deletes a user from the system by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('MANAGE_USER_ROLES')")
    @Operation(summary = "Update a user's roles", description = "Assigns a new set of roles to a user. This will overwrite all existing roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User roles updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid role name provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UserDto> updateUserRoles(@PathVariable Long id, @Valid @RequestBody UpdateUserRolesRequest request) {
        return ResponseEntity.ok(userService.updateUserRoles(id, request));
    }
}