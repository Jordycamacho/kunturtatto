package com.example.kunturtatto.service;

import java.util.List;
import java.util.Optional;

import com.example.kunturtatto.model.User;

public interface IUserService {
    
    List<User> findAll();
    User save(User user);
    User update(User user);
    void deleteById(Long idUser);
    Optional<User> findUserById(Long idUser);
    Optional<User> findUserByEmail(String email);

}
