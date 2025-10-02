package com.example.usermanagement.service;

import com.example.usermanagement.dto.SignInRequest;
import com.example.usermanagement.dto.SignInResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthServiceTest {

    @Test
    void signIn_successful() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        JwtService jwtService = Mockito.mock(JwtService.class);
        AuthenticationManager authManager = Mockito.mock(AuthenticationManager.class);

        User user = new User("user", "encoded");
        Mockito.when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        Mockito.when(jwtService.generateToken(user)).thenReturn("access");
        Mockito.when(jwtService.generateRefreshToken(user)).thenReturn("refresh");
        Mockito.when(jwtService.getJwtExpiration()).thenReturn(3600L);
        Mockito.when(jwtService.getRefreshExpiration()).thenReturn(86400000L);

        AuthService svc = new AuthService(userRepository, jwtService, authManager);
        SignInResponse resp = svc.signIn(new SignInRequest("user", "pass"));

        assertEquals("access", resp.token());
        assertEquals("refresh", resp.refreshToken());
    }
}
