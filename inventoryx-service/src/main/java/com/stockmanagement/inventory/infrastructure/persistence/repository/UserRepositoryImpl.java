package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.model.valueobject.UserId;
import com.stockmanagement.inventory.domain.model.valueobject.Username;
import com.stockmanagement.inventory.domain.repository.UserRepository;
import com.stockmanagement.inventory.infrastructure.persistence.entity.UserEntity;
import com.stockmanagement.inventory.infrastructure.persistence.mapper.UserEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserEntityMapper mapper;

    @Override
    public Optional<User> findById(UserId id) {
        return jpaUserRepository.findById(id.value().toString())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return jpaUserRepository.findByUsername(username.value())
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return jpaUserRepository.existsByUsername(username.value());
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = jpaUserRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public org.springframework.data.domain.Page<User> findAll(org.springframework.data.domain.Pageable pageable) {
        return jpaUserRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public org.springframework.data.domain.Page<User> findAll(String search,
            org.springframework.data.domain.Pageable pageable) {
        return jpaUserRepository
                .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        search, search, search, search, pageable)
                .map(mapper::toDomain);
    }
}
