package com.example.kunturtatto.mapper;

import com.example.kunturtatto.dto.RoleDto;
import com.example.kunturtatto.dto.UserDto;
import com.example.kunturtatto.model.Role;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.request.UserRequest;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-21T22:01:59+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.6 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

        userDto.id( user.getIdUser() );
        userDto.roles( mapRoles( user.getRoles() ) );
        userDto.email( user.getEmail() );
        userDto.registrationDate( user.getRegistrationDate() );

        return userDto.build();
    }

    @Override
    public User toUser(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.idUser( userDto.getId() );
        user.email( userDto.getEmail() );
        user.registrationDate( userDto.getRegistrationDate() );

        return user.build();
    }

    @Override
    public User toUser(UserRequest request) {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.email( request.getEmail() );

        return user.build();
    }

    @Override
    public RoleDto toRoleDto(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleDto.RoleDtoBuilder roleDto = RoleDto.builder();

        return roleDto.build();
    }
}
