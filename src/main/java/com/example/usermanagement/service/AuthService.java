package com.example.usermanagement.service;

import com.example.usermanagement.dto.RefreshTokenRequest;
import com.example.usermanagement.dto.SignInRequest;
import com.example.usermanagement.dto.SignInResponse;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.exception.TokenRefreshException;
import com.example.usermanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public SignInResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        long expiresIn = jwtService.getJwtExpiration();

        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(Instant.now().plusMillis(jwtService.getRefreshExpiration()));
        userRepository.save(user);

        return new SignInResponse(accessToken, "Bearer", expiresIn, refreshToken);
    }

    @Transactional
    public SignInResponse refreshToken(RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.refreshToken())
                .orElseThrow(() -> new TokenRefreshException("Refresh token not found."));

        if (user.getRefreshTokenExpiry().isBefore(Instant.now())) {
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user); // Persist the cleared token
            throw new TokenRefreshException("Refresh token has expired. Please sign in again.");
        }

        // Generate new access and refresh tokens (Rotation)
        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // Update the user's refresh token details in the database
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(Instant.now().plusMillis(jwtService.getRefreshExpiration()));
        userRepository.save(user);

        return new SignInResponse(newAccessToken, "Bearer", jwtService.getJwtExpiration(), newRefreshToken);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.refreshToken())
                .orElseThrow(() -> new UserNotFoundException("Refresh token not found."));

        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }
}