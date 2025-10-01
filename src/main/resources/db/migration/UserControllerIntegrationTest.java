package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CreateUserRequest;
import com.example.usermanagement.dto.SignInRequest;
import com.example.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        // Create a second user to test pagination and sorting
        userService.createUser(new CreateUserRequest("testuser", "password123"));

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
    void getAllUsers_shouldReturnPaginatedAndSortedResponse() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users?page=0&size=1&sort=username,asc")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                // Verify pagination metadata
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.totalPages", is(2)))
                .andExpect(jsonPath("$.size", is(1)))
                .andExpect(jsonPath("$.number", is(0)))
                // Verify content
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].username", is("admin"))); // 'admin' comes before 'testuser' alphabetically
    }
}