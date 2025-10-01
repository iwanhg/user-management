package com.example.usermanagement.service;

import com.example.usermanagement.dto.CreateUserRequest;
import com.example.usermanagement.dto.UpdateUserRequest;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.dto.UserDetailDto;
import com.example.usermanagement.dto.UpdateUserRolesRequest;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.exception.UserNotFoundException;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import com.example.usermanagement.mapper.ApplicationMapper;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ApplicationMapper mapper;

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        String hashedPassword = passwordEncoder.encode(request.password());
        User newUser = new User(request.username(), hashedPassword);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: ROLE_USER is not found."));
        newUser.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(newUser);
        return mapper.toUserDto(savedUser);
    }

    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::toUserDto);
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    public UserDetailDto getUserDetailsById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return mapper.toUserDetailDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (request.username() != null && !request.username().isBlank()) {
            user.setUsername(request.username());
        }
        if (request.password() != null && !request.password().isBlank()) {
            String hashedPassword = passwordEncoder.encode(request.password());
            user.setPassword(hashedPassword);
        }

        User updatedUser = userRepository.save(user);
        return mapper.toUserDto(updatedUser);
    }

    @Transactional
    public UserDto updateUserRoles(Long userId, UpdateUserRolesRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Set<Role> newRoles = request.roleNames().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        user.setRoles(newRoles);
        userRepository.save(user);

        return mapper.toUserDto(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
