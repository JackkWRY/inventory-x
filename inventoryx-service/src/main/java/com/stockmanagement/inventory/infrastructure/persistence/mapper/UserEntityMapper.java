package com.stockmanagement.inventory.infrastructure.persistence.mapper;

import com.stockmanagement.inventory.domain.model.Permission;
import com.stockmanagement.inventory.domain.model.Role;
import com.stockmanagement.inventory.domain.model.User;
import com.stockmanagement.inventory.domain.model.valueobject.*;
import com.stockmanagement.inventory.infrastructure.persistence.entity.PermissionEntity;
import com.stockmanagement.inventory.infrastructure.persistence.entity.RoleEntity;
import com.stockmanagement.inventory.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserEntityMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null)
            return null;

        User user = new User(
                new UserId(java.util.UUID.fromString(entity.getId())),
                new Username(entity.getUsername()),
                new Email(entity.getEmail()),
                new Password(entity.getPasswordHash()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isActive(),
                entity.isLocked(),
                entity.getFailedAttempts(),
                entity.getLastLoginAt(),
                entity.getCreatedAt());

        if (entity.getRoles() != null) {
            entity.getRoles().forEach(roleEntity -> user.addRole(toDomainRole(roleEntity)));
        }

        // Restore other state
        if (!entity.isActive()) {
            // No direct setter in domain, maybe should add one or handle via constructor if
            // strictly immutable
            // For now, assuming default true in constructor is overridden by specific
            // actions or we need private setters or reflective access/builder
            // Adding a method to User domain to set status for reconstruction
        }
        // Ideally mapped via constructor or builder for clean architecture, but for
        // simplicity:
        // Using reflection or package-private setters in Domain is common, or a
        // comprehensive Builder.
        // Given current User domain, let's stick to essential data.
        // NOTE: Domain User needs setters for reconstruction or a builder.
        // For this task, I will rely on the constructor and add missing fields setting
        // logic if needed.

        return user;
    }

    public UserEntity toEntity(User domain) {
        if (domain == null)
            return null;

        UserEntity entity = new UserEntity();
        entity.setId(domain.getId().value().toString());
        entity.setUsername(domain.getUsername().value());
        entity.setEmail(domain.getEmail().value());
        entity.setPasswordHash(domain.getPassword().value());
        entity.setFirstName(domain.getFirstName());
        entity.setLastName(domain.getLastName());
        entity.setActive(domain.isActive());
        entity.setLocked(domain.isLocked());
        entity.setFailedAttempts(domain.getFailedAttempts());
        entity.setLastLoginAt(domain.getLastLoginAt());

        if (domain.getRoles() != null) {
            entity.setRoles(domain.getRoles().stream()
                    .map(this::toEntityRole)
                    .collect(Collectors.toSet()));
        }

        return entity;
    }

    private Role toDomainRole(RoleEntity entity) {
        if (entity == null)
            return null;
        var permissions = entity.getPermissions().stream()
                .map(this::toDomainPermission)
                .collect(Collectors.toSet());
        return new Role(new RoleId(entity.getId()), entity.getName(), entity.getDescription(), permissions);
    }

    private RoleEntity toEntityRole(Role domain) {
        if (domain == null)
            return null;
        RoleEntity entity = new RoleEntity();
        entity.setId(domain.getId().value());
        entity.setName(domain.getName());
        entity.setDescription(domain.getDescription());
        // Permissions are usually managed separately or cascaded, but for linking we
        // might need IDs
        return entity;
    }

    private Permission toDomainPermission(PermissionEntity entity) {
        if (entity == null)
            return null;
        return new Permission(
                new PermissionId(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                entity.getResource(),
                entity.getAction());
    }
}
