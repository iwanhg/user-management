package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CreatePermissionRequest;
// ...existing imports...
import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.service.RolePermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoleAdminController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class RoleAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RolePermissionService rolePermissionService;

    @MockBean
    private com.example.usermanagement.service.JwtService jwtService;

    @Test
    void getAllRoles_returnsList() throws Exception {
        RoleDto role = new RoleDto(1, "ROLE_USER", java.util.Set.of());
        Mockito.when(rolePermissionService.getAllRoles()).thenReturn(List.of(role));

        mockMvc.perform(get("/api/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void createPermission_returnsCreated() throws Exception {
        PermissionDto perm = new PermissionDto(1, "CREATE_USER");
        Mockito.when(rolePermissionService.createPermission(any())).thenReturn(perm);

        CreatePermissionRequest req = new CreatePermissionRequest("CREATE_USER");

        mockMvc.perform(post("/api/admin/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("CREATE_USER"));
    }
}
