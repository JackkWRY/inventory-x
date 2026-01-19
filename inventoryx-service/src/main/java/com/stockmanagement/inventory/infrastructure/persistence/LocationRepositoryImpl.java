package com.stockmanagement.inventory.infrastructure.persistence;

import com.stockmanagement.inventory.domain.model.Location;
import com.stockmanagement.inventory.domain.model.LocationRepository;
import com.stockmanagement.inventory.domain.model.valueobject.LocationId;
import com.stockmanagement.inventory.infrastructure.persistence.entity.LocationEntity;
import com.stockmanagement.inventory.domain.model.Location.LocationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final JpaLocationRepository jpaRepository;

    @Override
    public Location save(Location location) {
        LocationEntity entity = toEntity(location);
        LocationEntity savedEntity = jpaRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Location> findById(LocationId id) {
        return jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Page<Location> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable).map(this::toDomain);
    }

    @Override
    public Page<Location> findAll(String search, Pageable pageable) {
        return jpaRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, pageable)
                .map(this::toDomain);
    }

    @Override
    public List<Location> findAllActive() {
        return jpaRepository.findByStatus(LocationStatus.ACTIVE).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    private LocationEntity toEntity(Location domain) {
        return new LocationEntity(
                domain.getId().value(),
                domain.getName(),
                domain.getType(),
                domain.getDescription(),
                domain.getAddress(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getUpdatedAt());
    }

    private Location toDomain(LocationEntity entity) {
        return new Location(
                new LocationId(entity.getId()),
                entity.getName(),
                entity.getType(),
                entity.getDescription(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}
