package com.example.usermanagement.controller;

import com.example.usermanagement.dto.RefreshTokenRequest;
import com.example.usermanagement.dto.SignInRequest;
import com.example.usermanagement.dto.SignInResponse;
import com.example.usermanagement.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private com.example.usermanagement.service.JwtService jwtService;

    @Test
    void signIn_returnsToken() throws Exception {
        SignInResponse response = new SignInResponse("access", "Bearer", 3600L, "refresh");
        Mockito.when(authService.signIn(any())).thenReturn(response);

        SignInRequest req = new SignInRequest("user", "pass");

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access"))
                .andExpect(jsonPath("$.refreshToken").value("refresh"));
    }

    @Test
    void refreshToken_returnsNewTokens() throws Exception {
        SignInResponse response = new SignInResponse("newAccess", "Bearer", 3600L, "newRefresh");
        Mockito.when(authService.refreshToken(any())).thenReturn(response);

        RefreshTokenRequest req = new RefreshTokenRequest("refresh");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("newAccess"))
                .andExpect(jsonPath("$.refreshToken").value("newRefresh"));
    }
}
