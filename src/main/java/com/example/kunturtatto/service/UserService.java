package com.example.kunturtatto.service;

import java.util.List;

import com.example.kunturtatto.dto.UserDto;
import com.example.kunturtatto.exception.*;
import com.example.kunturtatto.exception.EmailAlreadyExistsException;
import com.example.kunturtatto.request.UserRequest;

public interface UserService {
    UserDto createUser(UserRequest request) throws EmailAlreadyExistsException;
    UserDto updateUser(Long id, UserRequest request) throws ResourceNotFoundException, EmailAlreadyExistsException;
    UserDto getUserById(Long id) throws ResourceNotFoundException;
    UserDto getUserByEmail(String email) throws ResourceNotFoundException;
    List<UserDto> getAllUsers();
    UserDto getAuthenticatedUser();
    void deleteUser(Long id) throws ResourceNotFoundException;
    UserDto enableUser(Long id) throws ResourceNotFoundException;
    UserDto disableUser(Long id) throws ResourceNotFoundException;
}