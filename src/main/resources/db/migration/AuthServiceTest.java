package com.example.usermanagement.service;

import com.example.usermanagement.dto.RefreshTokenRequest;
import com.example.usermanagement.dto.SignInResponse;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.TokenRefreshException;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager; // Mocked but not used in these specific tests

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("testuser", "password", Role.USER);
        user.setRefreshToken("valid-refresh-token");
        user.setRefreshTokenExpiry(Instant.now().plusSeconds(3600));
    }

    @Test
    void refreshToken_shouldSucceedAndRotateToken_whenTokenIsValid() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(userRepository.findByRefreshToken("valid-refresh-token")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("new-access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("new-rotated-refresh-token");
        when(jwtService.getJwtExpiration()).thenReturn(3600000L);
        when(jwtService.getRefreshExpiration()).thenReturn(604800000L);

        // When
        SignInResponse response = authService.refreshToken(request);

        // Then
        assertThat(response.token()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-rotated-refresh-token");

        // Verify that the user's token was updated and saved
        verify(userRepository, times(1)).save(any(User.class));
        assertThat(user.getRefreshToken()).isEqualTo("new-rotated-refresh-token");
    }

    @Test
    void refreshToken_shouldThrowException_whenTokenIsNotFound() {
        // Given
        RefreshTokenRequest request = new RefreshTokenRequest("invalid-token");
        when(userRepository.findByRefreshToken("invalid-token")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(TokenRefreshException.class, () -> authService.refreshToken(request));
    }

    @Test
    void refreshToken_shouldThrowException_whenTokenIsExpired() {
        // Given
        user.setRefreshTokenExpiry(Instant.now().minusSeconds(1));
        RefreshTokenRequest request = new RefreshTokenRequest("valid-refresh-token");
        when(userRepository.findByRefreshToken("valid-refresh-token")).thenReturn(Optional.of(user));

        // When & Then
        assertThrows(TokenRefreshException.class, () -> authService.refreshToken(request));
        verify(userRepository, times(1)).save(user); // Verify it tries to save the user after expiry
    }
}