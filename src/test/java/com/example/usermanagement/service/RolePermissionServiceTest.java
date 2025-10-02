package com.example.usermanagement.service;

import com.example.usermanagement.dto.CreatePermissionRequest;
import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.mapper.ApplicationMapper;
import com.example.usermanagement.repository.PermissionRepository;
import com.example.usermanagement.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RolePermissionServiceTest {

    @Test
    void createPermission_successful() {
        RoleRepository roleRepo = Mockito.mock(RoleRepository.class);
        PermissionRepository permRepo = Mockito.mock(PermissionRepository.class);
        ApplicationMapper mapper = Mockito.mock(ApplicationMapper.class);

        Permission saved = new Permission(); saved.setId(1); saved.setName("CREATE_USER");
        Mockito.when(permRepo.findByName("CREATE_USER")).thenReturn(Optional.empty());
        Mockito.when(permRepo.save(Mockito.any())).thenReturn(saved);
        Mockito.when(mapper.toPermissionDto(saved)).thenReturn(new com.example.usermanagement.dto.PermissionDto(1, "CREATE_USER"));

        RolePermissionService svc = new RolePermissionService(roleRepo, permRepo, mapper);
        PermissionDto dto = svc.createPermission(new CreatePermissionRequest("CREATE_USER"));

        assertEquals("CREATE_USER", dto.name());
    }

    @Test
    void deletePermission_whenConstraint_thenThrows() {
        RoleRepository roleRepo = Mockito.mock(RoleRepository.class);
        PermissionRepository permRepo = Mockito.mock(PermissionRepository.class);
        ApplicationMapper mapper = Mockito.mock(ApplicationMapper.class);

        Mockito.doThrow(new DataIntegrityViolationException("fk"))
                .when(permRepo).deleteById(1);

        RolePermissionService svc = new RolePermissionService(roleRepo, permRepo, mapper);
        assertThrows(DataIntegrityViolationException.class, () -> svc.deletePermission(1));
    }
}
