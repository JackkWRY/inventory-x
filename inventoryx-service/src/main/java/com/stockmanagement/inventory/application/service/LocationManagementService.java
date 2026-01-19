package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.domain.model.Location;
import com.stockmanagement.inventory.domain.model.LocationRepository;
import com.stockmanagement.inventory.domain.model.valueobject.LocationId;
import com.stockmanagement.inventory.presentation.dto.CreateLocationRequest;
import com.stockmanagement.inventory.presentation.dto.UpdateLocationRequest;
import com.stockmanagement.inventory.domain.exception.LocationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationManagementService {

    private final LocationRepository locationRepository;

    @Transactional(readOnly = true)
    public Page<Location> getLocations(String search, Pageable pageable) {
        if (search != null && !search.isBlank()) {
            return locationRepository.findAll(search, pageable);
        }
        return locationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Location> getActiveLocations() {
        return locationRepository.findAllActive();
    }

    @Transactional(readOnly = true)
    public Location getLocation(String id) {
        return locationRepository.findById(new LocationId(id))
                .orElseThrow(() -> new LocationNotFoundException("Location not found with id: " + id));
    }

    @Transactional
    public Location createLocation(CreateLocationRequest request) {
        if (locationRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Location with name '" + request.getName() + "' already exists");
        }

        Location location = Location.create(
                request.getName(),
                request.getType(),
                request.getDescription(),
                request.getAddress());

        return locationRepository.save(location);
    }

    @Transactional
    public Location updateLocation(String id, UpdateLocationRequest request) {
        Location location = getLocation(id);

        // Check duplicate name if name changed
        if (!location.getName().equals(request.getName()) && locationRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Location with name '" + request.getName() + "' already exists");
        }

        location.updateDetails(
                request.getName(),
                request.getType() != null ? request.getType() : location.getType(),
                request.getDescription(),
                request.getAddress(),
                request.getStatus() != null ? request.getStatus() : location.getStatus());

        return locationRepository.save(location);
    }
}
