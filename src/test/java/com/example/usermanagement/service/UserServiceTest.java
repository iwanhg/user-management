package com.example.usermanagement.service;

import com.example.usermanagement.dto.CreateUserRequest;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import com.example.usermanagement.mapper.ApplicationMapper;
import com.example.usermanagement.repository.RoleRepository;
import com.example.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {

    @Test
    void createUser_assignsRoleAndReturnsDto() {
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);
        RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
        ApplicationMapper mapper = Mockito.mock(ApplicationMapper.class);

        Mockito.when(passwordEncoder.encode("password")).thenReturn("hashed");

        Role role = new Role(); role.setId(1); role.setName("ROLE_USER");
        Mockito.when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        User saved = new User("testuser", "hashed"); saved.setId(1L);
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(saved);

        UserDto dto = new UserDto(1L, "testuser", java.time.Instant.now(), java.time.Instant.now());
        Mockito.when(mapper.toUserDto(saved)).thenReturn(dto);

        UserService svc = new UserService(userRepository, passwordEncoder, roleRepository, mapper);
        UserDto res = svc.createUser(new CreateUserRequest("testuser", "password"));

        assertEquals("testuser", res.username());
        assertEquals(1L, res.id());
    }
}
