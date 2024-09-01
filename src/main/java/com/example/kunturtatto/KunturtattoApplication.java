package com.example.kunturtatto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.example.kunturtatto.model.Permission;
import com.example.kunturtatto.model.Role;
import com.example.kunturtatto.model.RoleEnum;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.UserRepository;
*/
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KunturtattoApplication {

        public static void main(String[] args) {
                SpringApplication.run(KunturtattoApplication.class, args);
        }
/* 
        @Bean
        CommandLineRunner init(UserRepository userRepository) {
                return args -> {
                        // Create PERMISSIONS 
                        Permission createPermission = Permission.builder()
                                        .name("CREATE")
                                        .build();

                        Permission readPermission = Permission.builder()
                                        .name("READ")
                                        .build();

                        Permission updatePermission = Permission.builder()
                                        .name("UPDATE")
                                        .build();

                        Permission deletePermission = Permission.builder()
                                        .name("DELETE")
                                        .build();

                        // Create ROLES
                        Role roleAdmin = Role.builder()
                                        .roleEnum(RoleEnum.ADMIN)
                                        .Permission(Set.of(createPermission, readPermission, updatePermission,
                                                        deletePermission))
                                        .build();

                        Role roleUser = Role.builder()
                                        .roleEnum(RoleEnum.USER)
                                        .Permission(Set.of(readPermission))
                                        .build();

                        // CREATE USERS
                        User userExample= User.builder()
                                        .email("example@gmail.com")
                                        .password("example")
                                        .isEnabled(true)
                                        .accountNoExpired(true)
                                        .accountNoLocked(true)
                                        .credentialNoExpired(true)
                                        .roles(Set.of(roleAdmin))
                                        .build();

                        User userexample2 = User.builder()
                                        .email("example2@gmail.com")
                                        .password("example2")
                                        .isEnabled(true)
                                        .accountNoExpired(true)
                                        .accountNoLocked(true)
                                        .credentialNoExpired(true)
                                        .roles(Set.of(roleUser))
                                        .build();

                        userRepository.saveAll(List.of(userExample, userExample2));
                };
        }

*/
}
