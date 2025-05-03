package com.example.kunturtatto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.kunturtatto.model.Role;
import com.example.kunturtatto.model.RoleEnum;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByRoleEnum(RoleEnum roleEnum);
    
    boolean existsByRoleEnum(RoleEnum roleEnum);
}