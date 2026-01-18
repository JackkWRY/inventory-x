package com.stockmanagement.inventory.domain.repository;

import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.model.valueobject.UserId;
import com.stockmanagement.inventory.domain.model.valueobject.Username;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId id);

    Optional<User> findByUsername(Username username);

    boolean existsByUsername(Username username);

    boolean existsByEmail(String email);

    User save(User user);

    org.springframework.data.domain.Page<User> findAll(org.springframework.data.domain.Pageable pageable);
}
