package com.example.usermanagement.service;

import com.example.usermanagement.dto.CreatePermissionRequest;
import com.example.usermanagement.dto.CreateRoleRequest;
import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.dto.UpdateRolePermissionsRequest;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.repository.PermissionRepository;
import com.example.usermanagement.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RolePermissionServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RolePermissionService rolePermissionService;

    private Role adminRole;
    private Permission createUserPermission;

    @BeforeEach
    void setUp() {
        adminRole = new Role();
        adminRole.setId(1);
        adminRole.setName("ROLE_ADMIN");

        createUserPermission = new Permission();
        createUserPermission.setId(1);
        createUserPermission.setName("CREATE_USER");
    }

    @Test
    void createRole_shouldSucceed_whenRoleNameIsUnique() {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("ROLE_NEW");
        when(roleRepository.findByName("ROLE_NEW")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setId(2);
            return role;
        });

        // When
        RoleDto result = rolePermissionService.createRole(request);

        // Then
        assertThat(result.name()).isEqualTo("ROLE_NEW");
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void createRole_shouldThrowException_whenRoleNameExists() {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("ROLE_ADMIN");
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.of(adminRole));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> rolePermissionService.createRole(request));
    }

    @Test
    void updateRolePermissions_shouldSucceed_whenRoleAndPermissionsExist() {
        // Given
        UpdateRolePermissionsRequest request = new UpdateRolePermissionsRequest(Set.of("CREATE_USER"));
        when(roleRepository.findById(1)).thenReturn(Optional.of(adminRole));
        when(permissionRepository.findByName("CREATE_USER")).thenReturn(Optional.of(createUserPermission));
        when(roleRepository.save(any(Role.class))).thenReturn(adminRole);

        // When
        rolePermissionService.updateRolePermissions(1, request);

        // Then
        verify(roleRepository, times(1)).save(adminRole);
        assertThat(adminRole.getPermissions()).contains(createUserPermission);
    }

    @Test
    void deletePermission_shouldSucceed_whenPermissionIsNotInUse() {
        // Given
        doNothing().when(permissionRepository).deleteById(1);

        // When
        rolePermissionService.deletePermission(1);

        // Then
        verify(permissionRepository, times(1)).deleteById(1);
    }

    @Test
    void deletePermission_shouldThrowException_whenPermissionIsInUse() {
        // Given
        doThrow(new DataIntegrityViolationException("")).when(permissionRepository).deleteById(1);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> rolePermissionService.deletePermission(1));
    }
}