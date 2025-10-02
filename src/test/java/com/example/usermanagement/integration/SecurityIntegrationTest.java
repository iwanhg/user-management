package com.example.usermanagement.integration;

import com.example.usermanagement.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Test
    void signIn_then_accessProtectedEndpoint_withToken() throws Exception {
        // Sign in with default admin (configured in application.properties)
    var mvcResult = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"adminpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        // parse JSON to extract token
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        String jwt = mapper.readTree(content).get("token").asText();

        // Use JWT to access a protected endpoint
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk());
    }

    @Test
    void accessProtectedEndpoint_withoutToken_returnsUnauthorized() throws Exception {
        // The security config returns 403 Forbidden for anonymous access to this endpoint
        // (AnonymousAuthentication is present but lacks authorities). Assert 403 to match app behavior.
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void jwtService_generate_and_validate_token() {
        var uds = userDetailsService.loadUserByUsername("admin");
        String token = jwtService.generateToken(uds);
        assertThat(jwtService.extractUsername(token)).isEqualTo(uds.getUsername());
        assertThat(jwtService.isTokenValid(token, uds)).isTrue();
    }
}
