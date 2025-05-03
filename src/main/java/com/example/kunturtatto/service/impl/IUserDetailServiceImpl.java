package com.example.kunturtatto.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.kunturtatto.model.User;
import com.example.kunturtatto.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IUserDetailServiceImpl implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findUserByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));
        user.getRoles().stream().flatMap(role -> role.getPermission().stream())
            .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));
        
        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            user.isEnabled(),
            user.isAccountNoExpired(),
            user.isCredentialNoExpired(),
            user.isAccountNoLocked(),
            authorities
        );
    }
}
