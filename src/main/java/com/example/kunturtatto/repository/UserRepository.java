package com.example.kunturtatto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kunturtatto.model.User;

public interface UserRepository  extends JpaRepository<User, Long>{

    Optional<User> findUserByEmail(String email);
} 
