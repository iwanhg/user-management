package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.dto.RoleDetailDto;
import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.dto.UserDetailDto;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationMapperTest {

    @Autowired
    private ApplicationMapper mapper;

    @Test
    void shouldMapUserToUserDto() {
        // Given
        User user = new User("testuser", "password");
        user.setId(1L);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        // When
        UserDto userDto = mapper.toUserDto(user);

        // Then
        assertThat(userDto).isNotNull();
        assertThat(userDto.id()).isEqualTo(user.getId());
        assertThat(userDto.username()).isEqualTo(user.getUsername());
        assertThat(userDto.createdAt()).isEqualTo(user.getCreatedAt());
        assertThat(userDto.updatedAt()).isEqualTo(user.getUpdatedAt());
    }

    @Test
    void shouldMapPermissionToPermissionDto() {
        // Given
        Permission permission = new Permission();
        permission.setId(1);
        permission.setName("CREATE_USER");

        // When
        PermissionDto permissionDto = mapper.toPermissionDto(permission);

        // Then
        assertThat(permissionDto).isNotNull();
        assertThat(permissionDto.id()).isEqualTo(permission.getId());
        assertThat(permissionDto.name()).isEqualTo(permission.getName());
    }

    @Test
    void shouldMapRoleToRoleDto() {
        // Given
        Permission permission = new Permission();
        permission.setName("READ_USERS");

        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");
        role.setPermissions(Set.of(permission));

        // When
        RoleDto roleDto = mapper.toRoleDto(role);

        // Then
        assertThat(roleDto).isNotNull();
        assertThat(roleDto.id()).isEqualTo(role.getId());
        assertThat(roleDto.name()).isEqualTo(role.getName());
        assertThat(roleDto.permissions()).hasSize(1);
        assertThat(roleDto.permissions().iterator().next().name()).isEqualTo("READ_USERS");
    }

    @Test
    void shouldMapUserToUserDetailDto() {
        // Given
        Permission permission = new Permission();
        permission.setName("READ_USERS");

        Role role = new Role();
        role.setName("ROLE_USER");
        role.setPermissions(Set.of(permission));

        User user = new User("detailuser", "password");
        user.setRoles(Set.of(role));

        // When
        UserDetailDto userDetailDto = mapper.toUserDetailDto(user);

        // Then
        assertThat(userDetailDto).isNotNull();
        assertThat(userDetailDto.username()).isEqualTo("detailuser");
        assertThat(userDetailDto.roles()).hasSize(1);
        assertThat(userDetailDto.roles().iterator().next().name()).isEqualTo("ROLE_USER");
        assertThat(userDetailDto.roles().iterator().next().permissions()).hasSize(1);
        assertThat(userDetailDto.roles().iterator().next().permissions().iterator().next().name()).isEqualTo("READ_USERS");
    }
}