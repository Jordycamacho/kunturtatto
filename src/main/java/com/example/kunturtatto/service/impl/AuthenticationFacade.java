package com.example.kunturtatto.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.UserRepository;
import com.example.kunturtatto.service.IAuthenticationFacade;

@Service
public class AuthenticationFacade implements IAuthenticationFacade {

    private final UserRepository userRepository;
    
    public AuthenticationFacade(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        String email = authentication.getName();
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
