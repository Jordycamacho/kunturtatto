package com.example.kunturtatto.config;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.kunturtatto.service.impl.IUserDetailServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class securityConfig {

    private static final Logger log = LoggerFactory.getLogger(securityConfig.class);


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/robots.txt").permitAll()
                        .requestMatchers("/admin/appointments/**").authenticated()
                        .requestMatchers("/api/cache/**").hasRole("ADMIN")
                        .requestMatchers("/Muthabara/**").permitAll()
                        .requestMatchers("/admin/**").authenticated()
                        .requestMatchers("/mail/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/Muthabara/ingresar")
                        .defaultSuccessUrl("/admin/disenos", true)
                        .successHandler((request, response, authentication) -> {
                            log.info("[SECURITY] Login exitoso para usuario: {}",
                                    authentication.getName());
                            log.info("[SECURITY] Roles: {}",
                                    authentication.getAuthorities().stream()
                                            .map(Object::toString)
                                            .collect(Collectors.joining(", ")));
                            response.sendRedirect("/admin/disenos");
                        })
                        .failureHandler((request, response, exception) -> {
                            String email = request.getParameter("username");
                            log.warn("[SECURITY] Login fallido para usuario: {}, IP: {}, Razón: {}",
                                    email,
                                    request.getRemoteAddr(),
                                    exception.getMessage());
                            response.sendRedirect("/Muthabara/ingresar?error=true");
                        })
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/Muthabara/logout")
                        .logoutSuccessUrl("/Muthabara")
                        .addLogoutHandler((request, response, authentication) -> {
                            if (authentication != null) {
                                log.info("[SECURITY] Logout exitoso para usuario: {}",
                                        authentication.getName());
                            }
                        })
                        .invalidateHttpSession(true)
                        .permitAll());
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(IUserDetailServiceImpl userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
