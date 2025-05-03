package com.example.kunturtatto.dto;

import java.util.Set;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    private Long id;
    private String name;
    private Set<PermissionDto> permissions;
}