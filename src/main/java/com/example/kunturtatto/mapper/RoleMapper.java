package com.example.kunturtatto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.example.kunturtatto.dto.PermissionDto;
import com.example.kunturtatto.dto.RoleDto;
import com.example.kunturtatto.model.Permission;
import com.example.kunturtatto.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);

    RoleDto toRoleDto(Role role);
    
    Role toRole(RoleDto roleDto);
    
    PermissionDto toPermissionDto(Permission permission);
    
    Permission toPermission(PermissionDto permissionDto);
}