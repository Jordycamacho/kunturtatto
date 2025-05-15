package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.PermissionDto;
import com.example.kunturtatto.dto.RoleDto;
import com.example.kunturtatto.model.Permission;
import com.example.kunturtatto.model.Role;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-15T16:08:09+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleDto toRoleDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDto.RoleDtoBuilder roleDto = RoleDto.builder();

        return roleDto.build();
    }

    @Override
    public Role toRole(RoleDto roleDto) {
        if ( roleDto == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        return role.build();
    }

    @Override
    public PermissionDto toPermissionDto(Permission permission) {
        if ( permission == null ) {
            return null;
        }

        PermissionDto.PermissionDtoBuilder permissionDto = PermissionDto.builder();

        permissionDto.name( permission.getName() );
        permissionDto.description( permission.getDescription() );

        return permissionDto.build();
    }

    @Override
    public Permission toPermission(PermissionDto permissionDto) {
        if ( permissionDto == null ) {
            return null;
        }

        Permission.PermissionBuilder permission = Permission.builder();

        permission.name( permissionDto.getName() );
        permission.description( permissionDto.getDescription() );

        return permission.build();
    }
}
