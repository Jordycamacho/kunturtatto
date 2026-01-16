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

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    @Caching(evict = {
            @CacheEvict(value = "usersAll", allEntries = true),
            @CacheEvict(value = "userByEmail", key = "#request.email")
    })
    public UserDto createUser(UserRequest request) throws EmailAlreadyExistsException {
        log.info("[USER_SERVICE] Iniciando creación de usuario para email: {}", request.getEmail());

        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            log.warn("[USER_SERVICE] Email ya registrado: {}", request.getEmail());
            throw new EmailAlreadyExistsException("El email ya está registrado");
        }

        log.debug("[USER_SERVICE] Codificando contraseña para usuario: {}", request.getEmail());
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRegistrationDate(new Date());
        user.setEnabled(true);
        user.setAccountNoExpired(true);
        user.setAccountNoLocked(true);
        user.setCredentialNoExpired(true);

        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            log.debug("[USER_SERVICE] Asignando roles al usuario: {}", request.getEmail());
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setRoles(roles);
            log.debug("[USER_SERVICE] Roles asignados: {}", roles.stream()
                    .map(role -> role.getRoleEnum().name())
                    .collect(Collectors.joining(", ")));
        }

        User savedUser = userRepository.save(user);
        log.info("[USER_SERVICE] Usuario creado exitosamente - ID: {}, Email: {}",
                savedUser.getIdUser(), savedUser.getEmail());
        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersAll", allEntries = true),
            @CacheEvict(value = "userById", key = "#id"),
            @CacheEvict(value = "userByEmail", allEntries = true),
            @CacheEvict(value = "authenticatedUser", allEntries = true)
    })
    public UserDto updateUser(Long id, UserRequest request)
            throws ResourceNotFoundException, EmailAlreadyExistsException {

        log.info("[USER_SERVICE] Iniciando actualización de usuario ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[USER_SERVICE] Usuario no encontrado para actualización - ID: {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado", null, id);
                });

        log.debug("[USER_SERVICE] Usuario encontrado: {} (Email actual: {})",
                user.getIdUser(), user.getEmail());

        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
                log.warn("[USER_SERVICE] Email ya en uso: {}", request.getEmail());
                throw new EmailAlreadyExistsException("El email ya está registrado");
            }
            log.info("[USER_SERVICE] Cambiando email de {} a {}",
                    user.getEmail(), request.getEmail());
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            log.debug("[USER_SERVICE] Actualizando contraseña para usuario ID: {}", id);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleIds() != null) {
            log.debug("[USER_SERVICE] Actualizando roles para usuario ID: {}", id);
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            user.setRoles(roles);
            log.debug("[USER_SERVICE] Nuevos roles: {}", roles.stream()
                    .map(role -> role.getRoleEnum().name())
                    .collect(Collectors.joining(", ")));
        }

        User updatedUser = userRepository.save(user);
        log.info("[USER_SERVICE] Usuario actualizado exitosamente - ID: {}", id);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userById", key = "#id", unless = "#result == null")
    public UserDto getUserById(Long id) throws ResourceNotFoundException {
        log.info("[USER_SERVICE] Obteniendo usuario por ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[USER_SERVICE] Usuario no encontrado - ID: {}", id);
                    return new ResourceNotFoundException("Usuario no encontrado", null, id);
                });

        log.debug("[USER_SERVICE] Cache miss para userById: {}, obteniendo de BD", id);
        log.info("[USER_SERVICE] Usuario encontrado - ID: {}, Email: {}",
                user.getIdUser(), user.getEmail());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userByEmail", key = "#email", unless = "#result == null")
    public UserDto getUserByEmail(String email) throws ResourceNotFoundException {
        log.info("[USER_SERVICE] Obteniendo usuario por email: {}", email);

        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> {
                    log.error("[USER_SERVICE] Usuario no encontrado - Email: {}", email);
                    return new ResourceNotFoundException("Usuario no encontrado", email, null);
                });

        log.debug("[USER_SERVICE] Cache miss para userByEmail: {}, obteniendo de BD", email);
        log.info("[USER_SERVICE] Usuario encontrado - Email: {}, ID: {}",
                email, user.getIdUser());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "usersAll", unless = "#result.isEmpty()")
    public List<UserDto> getAllUsers() {
        log.info("[USER_SERVICE] Obteniendo todos los usuarios");

        List<UserDto> users = userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());

        log.debug("[USER_SERVICE] Cache miss para usersAll, obteniendo de BD");
        log.info("[USER_SERVICE] Total de usuarios encontrados: {}", users.size());
        return users;
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersAll", allEntries = true),
            @CacheEvict(value = "userById", key = "#id"),
            @CacheEvict(value = "userByEmail", allEntries = true),
            @CacheEvict(value = "authenticatedUser", allEntries = true)
    })
    public void deleteUser(Long id) throws ResourceNotFoundException {
        log.info("[USER_SERVICE] Iniciando eliminación de usuario ID: {}", id);

        if (!userRepository.existsById(id)) {
            log.error("[USER_SERVICE] Usuario no encontrado para eliminar - ID: {}", id);
            throw new ResourceNotFoundException("Usuario no encontrado", null, id);
        }

        userRepository.deleteById(id);
        log.warn("[USER_SERVICE] Usuario eliminado - ID: {}", id);
    }

    @Override
    @Transactional
    public UserDto enableUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado", null, id));
        user.setEnabled(true);
        User updatedUser = userRepository.save(user);
        log.info("Usuario habilitado con ID: {}", id);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "usersAll", allEntries = true),
            @CacheEvict(value = "userById", key = "#id"),
            @CacheEvict(value = "authenticatedUser", allEntries = true)
    })
    public UserDto disableUser(Long id) throws ResourceNotFoundException {
        log.info("[USER_SERVICE] Habilitando usuario ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado", null, id));
        user.setEnabled(false);
        User updatedUser = userRepository.save(user);
        log.info("Usuario deshabilitado con ID: {}", id);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "authenticatedUser", key = "#root.method.name", unless = "#result == null")
    public UserDto getAuthenticatedUser() {
        log.info("[USER_SERVICE] Obteniendo usuario autenticado");

        User user = authenticationFacade.getAuthenticatedUser();

        if (user == null) {
            log.warn("[USER_SERVICE] No hay usuario autenticado");
            return null;
        }

        log.debug("[USER_SERVICE] Cache miss para authenticatedUser, obteniendo de contexto de seguridad");
        log.info("[USER_SERVICE] Usuario autenticado obtenido - ID: {}, Email: {}",
                user.getIdUser(), user.getEmail());
        return userMapper.toUserDto(user);
    }
}