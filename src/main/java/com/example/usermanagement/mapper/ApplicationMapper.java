package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.PermissionDto;
import com.example.usermanagement.dto.RoleDetailDto;
import com.example.usermanagement.dto.RoleDto;
import com.example.usermanagement.dto.UserDetailDto;
import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.entity.Permission;
import com.example.usermanagement.entity.Role;
import com.example.usermanagement.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    UserDto toUserDto(User user);
    UserDetailDto toUserDetailDto(User user);
    RoleDto toRoleDto(Role role);
    RoleDetailDto toRoleDetailDto(Role role);
    PermissionDto toPermissionDto(Permission permission);

}