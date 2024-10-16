package com.example.kunturtatto.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.UserRepository;
import com.example.kunturtatto.service.IUserService;
import com.example.kunturtatto.exception.UserNotFoundException;

/**
 * Servicio para gestionar operaciones de usuario.
 */
@Service
public class IUserServiceImpl implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        if (!userRepository.existsById(user.getIdUser())) {
            throw new UserNotFoundException("Usuario con ID " + user.getIdUser() + " no encontrado.");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteById(Long idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new UserNotFoundException("Usuario con ID " + idUser + " no encontrado.");
        }
        userRepository.deleteById(idUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long idUser) {
        return userRepository.findById(idUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
