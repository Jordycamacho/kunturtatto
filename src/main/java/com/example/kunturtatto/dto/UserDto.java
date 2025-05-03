package com.example.kunturtatto.dto;

import lombok.*;

import java.util.Date;
import java.util.Set;

@Data
@Builder
public class UserDto {
    private Long id;
    private String email;
    private Date registrationDate;
    private boolean isEnabled;
    private Set<RoleDto> roles;
}