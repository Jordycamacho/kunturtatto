package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.RoleDto;
import com.example.kunturtatto.dto.UserDto;
import com.example.kunturtatto.model.Role;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.request.UserRequest;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roles", source = "roles", qualifiedByName = "mapRoles")
    UserDto toUserDto(User user);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toUser(UserDto userDto);
    
    User toUser(UserRequest request);

    @Named("mapRoles")
    default Set<RoleDto> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(this::toRoleDto)
                .collect(Collectors.toSet());
    }

    RoleDto toRoleDto(Role role);
}