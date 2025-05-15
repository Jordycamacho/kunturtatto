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
    date = "2025-05-15T16:08:09+0200",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto.UserDtoBuilder userDto = UserDto.builder();

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
        user.password( request.getPassword() );

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
