package com.stockmanagement.inventory.infrastructure.security;

import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.repository.UserRepository;
import com.stockmanagement.inventory.domain.model.valueobject.Username;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(new Username(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        var authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());

        // Add Roles as Authorities (ROLE_PREFIX)
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername().value())
                .password(user.getPassword().value())
                .disabled(!user.isActive())
                .accountLocked(user.isLocked())
                .credentialsExpired(false)
                .accountExpired(false)
                .authorities(authorities)
                .build();
    }
}
