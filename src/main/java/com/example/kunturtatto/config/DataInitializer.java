package com.example.kunturtatto.config;

import com.example.kunturtatto.model.*;
import com.example.kunturtatto.repository.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void run(String... args) {
        try {
            initializeData();
        } catch (Exception e) {
            log.error("Error inicializando datos", e);
        }
    }

    private void initializeData() {
        Permission readPerm = createPermissionIfNotExists("READ", "Permiso de lectura");
        Permission createPerm = createPermissionIfNotExists("CREATE", "Permiso de creación");
        Permission updatePerm = createPermissionIfNotExists("UPDATE", "Permiso de actualización");
        Permission deletePerm = createPermissionIfNotExists("DELETE", "Permiso de eliminación");

        Role adminRole = createRoleIfNotExists(
            RoleEnum.ROLE_ADMIN, 
            Set.of(readPerm, createPerm, updatePerm, deletePerm)
        );
        
        Role userRole = createRoleIfNotExists(
            RoleEnum.ROLE_USER, 
            Set.of(readPerm)
        );

        // 3. Crear usuarios iniciales
        createUserIfNotExists(
            "kunturtattoo@gmail.com", 
            "kunturtattoo012890.", 
            adminRole
        );
        
        createUserIfNotExists(
            "jordycamacho225@gmail.com", 
            "012890", 
            userRole
        );
    }

    private Permission createPermissionIfNotExists(String name, String description) {
        return permissionRepository.findByName(name)
            .orElseGet(() -> {
                Permission newPerm = Permission.builder()
                    .name(name)
                    .description(description)
                    .build();
                return permissionRepository.save(newPerm);
            });
    }

    private Role createRoleIfNotExists(RoleEnum roleEnum, Set<Permission> permissions) {
        return roleRepository.findByRoleEnum(roleEnum)
            .orElseGet(() -> {
                Role newRole = Role.builder()
                    .roleEnum(roleEnum)
                    .Permission(permissions)
                    .build();
                return roleRepository.save(newRole);
            });
    }

    private void createUserIfNotExists(String email, String password, Role role) {
        if (userRepository.findUserByEmail(email).isEmpty()) {
            User newUser = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .registrationDate(new Date())
                .isEnabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .roles(Set.of(role))
                .build();
            
            userRepository.save(newUser);
            log.info("Usuario creado: {}", email);
        }
    }
}