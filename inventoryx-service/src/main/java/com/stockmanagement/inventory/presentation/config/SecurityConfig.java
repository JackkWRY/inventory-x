package com.stockmanagement.inventory.presentation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SecurityConfig - Security configuration for development.
 * 
 * DEVELOPMENT MODE:
 * ================
 * Disables security for easier testing.
 * 
 * PRODUCTION:
 * ==========
 * Enable proper authentication and authorization.
 * 
 * @author InventoryX Development Team
 * @since 2026-01-13
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll());
        return http.build();
    }
}
