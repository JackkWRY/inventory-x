package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.LocationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.List;

public interface LocationRepository {
    Location save(Location location);

    Optional<Location> findById(LocationId id);

    Page<Location> findAll(Pageable pageable);

    Page<Location> findAll(String search, Pageable pageable);

    List<Location> findAllActive();

    boolean existsByName(String name);
}
