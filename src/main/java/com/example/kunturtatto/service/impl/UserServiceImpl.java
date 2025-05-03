package com.example.kunturtatto.service.impl;

import com.example.kunturtatto.dto.UserDto;
import com.example.kunturtatto.exception.EmailAlreadyExistsException;
import com.example.kunturtatto.exception.ResourceNotFoundException;
import com.example.kunturtatto.mapper.UserMapper;
import com.example.kunturtatto.model.Role;
import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.RoleRepository;
import com.example.kunturtatto.repository.UserRepository;
import com.example.kunturtatto.request.UserRequest;
import com.example.kunturtatto.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationFacade authenticationFacade;

    @Override
    @Transactional
    public UserDto createUser(UserRequest request) throws EmailAlreadyExistsException {
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("El email ya está registrado");
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegistrationDate(new Date());
        user.setEnabled(true);
        user.setAccountNoExpired(true);
        user.setAccountNoLocked(true);
        user.setCredentialNoExpired(true);

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setRoles(roles);
        }

        User savedUser = userRepository.save(user);
        log.info("Usuario creado con ID: {}", savedUser.getIdUser());
        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserRequest request) throws ResourceNotFoundException, EmailAlreadyExistsException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

                if (!user.getEmail().equals(request.getEmail())) {
                    if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
                        throw new EmailAlreadyExistsException("El email ya está registrado");
                    }
                    user.setEmail(request.getEmail());
                }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        log.info("Usuario actualizado con ID: {}", id);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) throws ResourceNotFoundException {
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws ResourceNotFoundException {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
        log.info("Usuario eliminado con ID: {}", id);
    }

    @Override
    @Transactional
    public UserDto enableUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        log.info("Usuario habilitado con ID: {}", id);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto disableUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        log.info("Usuario deshabilitado con ID: {}", id);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getAuthenticatedUser() {
        User user = authenticationFacade.getAuthenticatedUser();
        return userMapper.toUserDto(user);
    }
}