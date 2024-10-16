package com.example.kunturtatto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kunturtatto.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * 
     * @param email Email del usuario
     * @return Objeto Optional que puede contener el usuario encontrado
     */
    Optional<User> findUserByEmail(String email);
}
