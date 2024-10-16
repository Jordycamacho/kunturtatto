package com.example.kunturtatto.dtos;

import lombok.Data;
import java.util.Set;

@Data
public class UserDTO {
    private Long idUser;
    private String email;
    private String password;
    private boolean isEnabled;
    private Set<Long> roleIds;
}
