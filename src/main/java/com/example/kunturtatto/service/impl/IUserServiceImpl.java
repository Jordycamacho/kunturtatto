package com.example.kunturtatto.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.UserRepository;
import com.example.kunturtatto.service.IUserService;
import com.example.kunturtatto.exception.UserNotFoundException;

@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        // Verificar si el usuario existe antes de actualizar
        if (!userRepository.existsById(user.getIdUser())) {
            throw new UserNotFoundException("User with ID " + user.getIdUser() + " not found.");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new UserNotFoundException("User with ID " + idUser + " not found.");
        }
        userRepository.deleteById(idUser);
    }

    @Override
    public Optional<User> findUserById(Long idUser) {
        return userRepository.findById(idUser);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
