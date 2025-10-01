package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CreatePermissionRequest;
import com.example.usermanagement.dto.CreateRoleRequest;
import com.example.usermanagement.dto.SignInRequest;
import com.example.usermanagement.dto.UpdateRolePermissionsRequest;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.repository.UserRepository;
import com.example.usermanagement.repository.PermissionRepository;
import com.example.usermanagement.repository.RoleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RoleAdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        // Sign in as the default admin to get a valid JWT
        SignInRequest signInRequest = new SignInRequest("admin", "adminpassword");
        MvcResult result = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode responseJson = objectMapper.readTree(responseBody);
        adminToken = responseJson.get("token").asText();
    }

    @Test
    @WithAnonymousUser
    void getAllRoles_asAnonymousUser_shouldBeForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/roles"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllRoles_asAdmin_shouldSucceed() throws Exception {
        mockMvc.perform(get("/api/admin/roles")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // ROLE_ADMIN, ROLE_USER
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].permissions").exists());
    }

    @Test
    void getRoleDetailsById_asAdmin_shouldSucceedAndContainUsers() throws Exception {
        // The default 'admin' user has the 'ROLE_ADMIN'. Let's fetch details for that role.
        Integer adminRoleId = roleRepository.findByName("ROLE_ADMIN").get().getId();

        mockMvc.perform(get("/api/admin/roles/" + adminRoleId + "/details")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ROLE_ADMIN")))
                .andExpect(jsonPath("$.permissions").isArray())
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users", hasSize(1)))
                .andExpect(jsonPath("$.users[0].username", is("admin")));
    }


    @Test
    void createRole_and_updatePermissions_and_deletePermission_flow() throws Exception {
        // 1. Create a new Permission
        CreatePermissionRequest createPermRequest = new CreatePermissionRequest("CAN_DO_SPECIAL_THING");
        MvcResult createPermResult = mockMvc.perform(post("/api/admin/permissions")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPermRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("CAN_DO_SPECIAL_THING")))
                .andReturn();

        Permission permission = objectMapper.readValue(createPermResult.getResponse().getContentAsString(), Permission.class);

        // 2. Create a new Role
        CreateRoleRequest createRoleRequest = new CreateRoleRequest("ROLE_SPECIAL_AGENT");
        MvcResult createRoleResult = mockMvc.perform(post("/api/admin/roles")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRoleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("ROLE_SPECIAL_AGENT")))
                .andExpect(jsonPath("$.permissions", hasSize(0)))
                .andReturn();

        JsonNode roleJson = objectMapper.readTree(createRoleResult.getResponse().getContentAsString());
        Integer roleId = roleJson.get("id").asInt();

        // 3. Update the new Role with the new Permission
        UpdateRolePermissionsRequest updateRequest = new UpdateRolePermissionsRequest(Set.of("CAN_DO_SPECIAL_THING"));
        mockMvc.perform(put("/api/admin/roles/" + roleId + "/permissions")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("ROLE_SPECIAL_AGENT")))
                .andExpect(jsonPath("$.permissions", hasSize(1)))
                .andExpect(jsonPath("$.permissions[0].name", is("CAN_DO_SPECIAL_THING")));

        // 4. Try to delete the permission while it's in use (should fail with 409 Conflict)
        mockMvc.perform(delete("/api/admin/permissions/" + permission.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isConflict());

        // 5. Remove the permission from the role
        UpdateRolePermissionsRequest emptyRequest = new UpdateRolePermissionsRequest(Set.of());
        mockMvc.perform(put("/api/admin/roles/" + roleId + "/permissions")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.permissions", hasSize(0)));

        // 6. Delete the permission now that it's not in use (should succeed)
        mockMvc.perform(delete("/api/admin/permissions/" + permission.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }
}