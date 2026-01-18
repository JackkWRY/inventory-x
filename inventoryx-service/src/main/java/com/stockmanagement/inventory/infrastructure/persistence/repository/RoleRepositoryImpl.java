package com.stockmanagement.inventory.infrastructure.persistence.repository;

import com.stockmanagement.inventory.domain.model.Role;
import com.stockmanagement.inventory.domain.model.valueobject.RoleId;
import com.stockmanagement.inventory.domain.repository.RoleRepository;
import com.stockmanagement.inventory.infrastructure.persistence.entity.RoleEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class RoleRepositoryImpl implements RoleRepository {

        private final JpaRoleRepository jpaRoleRepository;
        // We can reuse parts of UserEntityMapper or create RoleEntityMapper.
        // For simplicity, I'll extract logic from UserEntityMapper or duplicate
        // slightly for Role
        // Actually, let's implement a simple internal mapping here or add public Role
        // mapping to UserEntityMapper
        // Checking UserEntityMapper... it has toDomainRole but private. I should make
        // it public or duplicate.
        // I previously made it private. Let's make it reflectively accessible or just
        // manual map here.
        // Manual map is cleaner for now to avoid modifying Mapper again immediately.

        @Override
        public Optional<Role> findById(RoleId id) {
                return jpaRoleRepository.findById(id.value())
                                .map(this::toDomain);
        }

        @Override
        public Optional<Role> findByName(String name) {
                return jpaRoleRepository.findByName(name)
                                .map(this::toDomain);
        }

        private Role toDomain(RoleEntity entity) {
                // Simple mapping, Permission mapping is also needed
                // Assuming we need permissions too
                // Since I don't have a public Mapper for Role, and I want to be clean:
                // Ideally I should update UserEntityMapper to be more general 'AuthMapper' or
                // make methods public.
                // I will do manual mapping here for now to proceed, referencing the Entity
                // structure.
                return new Role(
                                new RoleId(entity.getId()),
                                entity.getName(),
                                entity.getDescription(),
                                entity.getPermissions().stream()
                                                .map(p -> new com.stockmanagement.inventory.domain.model.Permission(
                                                                new com.stockmanagement.inventory.domain.model.valueobject.PermissionId(
                                                                                p.getId()),
                                                                p.getName(),
                                                                p.getDescription(),
                                                                p.getResource(),
                                                                p.getAction()))
                                                .collect(Collectors.toSet()));
        }
}
