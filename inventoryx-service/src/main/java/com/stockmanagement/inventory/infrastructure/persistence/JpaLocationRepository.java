package com.stockmanagement.inventory.infrastructure.persistence;

import com.stockmanagement.inventory.infrastructure.persistence.entity.LocationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.stockmanagement.inventory.domain.model.Location.LocationStatus;

@Repository
public interface JpaLocationRepository extends JpaRepository<LocationEntity, String> {
        boolean existsByName(String name);

        List<LocationEntity> findByStatus(LocationStatus status);

        Page<LocationEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name,
                        String description, Pageable pageable);
}
