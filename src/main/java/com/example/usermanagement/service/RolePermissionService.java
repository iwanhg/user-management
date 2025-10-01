package com.example.usermanagement.service;

import com.example.usermanagement.dto.CreatePermissionRequest;
import com.example.usermanagement.dto.CreateRoleRequest;
import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.dto.RoleDetailDto;
import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.dto.UpdateRolePermissionsRequest;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.repository.PermissionRepository;
import com.example.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import com.example.usermanagement.mapper.ApplicationMapper;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ApplicationMapper mapper;

    @Transactional(readOnly = true)
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(mapper::toRoleDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoleDetailDto getRoleDetailsById(Integer roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
        return mapper.toRoleDetailDto(role);
    }

    @Transactional(readOnly = true)
    public List<PermissionDto> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(mapper::toPermissionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        if (roleRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Role with name '" + request.name() + "' already exists.");
        }
        Role newRole = new Role();
        newRole.setName(request.name());
        return mapper.toRoleDto(roleRepository.save(newRole));
    }

    @Transactional
    public RoleDto updateRolePermissions(Integer roleId, UpdateRolePermissionsRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        Set<Permission> newPermissions = request.permissionNames().stream()
                .map(permissionName -> permissionRepository.findByName(permissionName)
                        .orElseThrow(() -> new RuntimeException("Permission not found: " + permissionName)))
                .collect(Collectors.toSet());

        role.setPermissions(newPermissions);
        return mapper.toRoleDto(roleRepository.save(role));
    }

    @Transactional
    public PermissionDto createPermission(CreatePermissionRequest request) {
        if (permissionRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Permission with name '" + request.name() + "' already exists.");
        }
        Permission newPermission = new Permission();
        newPermission.setName(request.name());
        return mapper.toPermissionDto(permissionRepository.save(newPermission));
    }

    @Transactional
    public void deletePermission(Integer permissionId) {
        try {
            permissionRepository.deleteById(permissionId);
        } catch (DataIntegrityViolationException e) {
            throw new DataIntegrityViolationException("Cannot delete permission. It is currently assigned to one or more roles.", e);
        }
    }
}