package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CreateRoleRequest;
import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.dto.RoleDetailDto;
import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.dto.UpdateRolePermissionsRequest;
import com.example.usermanagement.service.JwtService;
import com.example.usermanagement.service.RolePermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleAdminController.class)
class RoleAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RolePermissionService rolePermissionService;

    // Dependencies of the security config that must be mocked
    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @WithAnonymousUser
    void getAllRoles_asAnonymous_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = "MANAGE_AUTHORIZATION")
    void getAllRoles_asAdmin_shouldSucceed() throws Exception {
        // Given
        RoleDto roleDto = new RoleDto(1, "ROLE_ADMIN", Set.of());
        when(rolePermissionService.getAllRoles()).thenReturn(List.of(roleDto));

        // When & Then
        mockMvc.perform(get("/api/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "MANAGE_AUTHORIZATION")
    void getRoleDetailsById_asAdmin_shouldSucceed() throws Exception {
        // Given
        RoleDetailDto roleDetailDto = new RoleDetailDto(1, "ROLE_ADMIN", Set.of(), Set.of());
        when(rolePermissionService.getRoleDetailsById(1)).thenReturn(roleDetailDto);

        // When & Then
        mockMvc.perform(get("/api/admin/roles/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ROLE_ADMIN"));
    }

    @Test
    @WithMockUser(authorities = "MANAGE_AUTHORIZATION")
    void getAllPermissions_asAdmin_shouldSucceed() throws Exception {
        // Given
        PermissionDto permissionDto = new PermissionDto(1, "CREATE_USER");
        when(rolePermissionService.getAllPermissions()).thenReturn(List.of(permissionDto));

        // When & Then
        mockMvc.perform(get("/api/admin/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("CREATE_USER"));
    }

    @Test
    @WithMockUser(authorities = "MANAGE_AUTHORIZATION")
    void createRole_asAdmin_shouldSucceed() throws Exception {
        // Given
        CreateRoleRequest request = new CreateRoleRequest("ROLE_TEST");
        RoleDto createdRole = new RoleDto(3, "ROLE_TEST", Set.of());
        when(rolePermissionService.createRole(any(CreateRoleRequest.class))).thenReturn(createdRole);

        // When & Then
        mockMvc.perform(post("/api/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("ROLE_TEST"));
    }

    @Test
    @WithMockUser(authorities = "MANAGE_AUTHORIZATION")
    void updateRolePermissions_asAdmin_shouldSucceed() throws Exception {
        // Given
        UpdateRolePermissionsRequest request = new UpdateRolePermissionsRequest(Set.of("CREATE_USER"));
        RoleDto updatedRole = new RoleDto(1, "ROLE_ADMIN", Set.of(new PermissionDto(1, "CREATE_USER")));
        when(rolePermissionService.updateRolePermissions(eq(1), any(UpdateRolePermissionsRequest.class))).thenReturn(updatedRole);

        // When & Then
        mockMvc.perform(put("/api/admin/roles/1/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions[0].name").value("CREATE_USER"));
    }
}