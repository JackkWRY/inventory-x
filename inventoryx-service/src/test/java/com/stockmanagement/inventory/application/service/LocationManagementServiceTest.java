package com.stockmanagement.inventory.application.service;

import com.stockmanagement.inventory.domain.model.Location;
import com.stockmanagement.inventory.domain.model.Location.LocationType;
import com.stockmanagement.inventory.domain.model.LocationRepository;
import com.stockmanagement.inventory.domain.model.valueobject.LocationId;
import com.stockmanagement.inventory.presentation.dto.CreateLocationRequest;
import com.stockmanagement.inventory.presentation.dto.UpdateLocationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationManagementServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private LocationManagementService locationManagementService;

    private Location location;

    @BeforeEach
    void setUp() {
        location = Location.create(
                "Warehouse A", LocationType.WAREHOUSE, "Main Warehouse", "123 Main St");
    }

    @Test
    void shouldCreateLocationSuccessfully() {
        // Arrange
        CreateLocationRequest request = new CreateLocationRequest();
        request.setName("Store B");
        request.setType(LocationType.STORE);
        request.setDescription("Retail Store");
        request.setAddress("456 Market St");

        when(locationRepository.existsByName("Store B")).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        // Act
        Location created = locationManagementService.createLocation(request);

        // Assert
        assertNotNull(created);
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    void shouldFailCreateIfNameExists() {
        // Arrange
        CreateLocationRequest request = new CreateLocationRequest();
        request.setName("Warehouse A"); // Existing name

        when(locationRepository.existsByName("Warehouse A")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> locationManagementService.createLocation(request));
    }

    @Test
    void shouldUpdateLocation() {
        // Arrange
        String locationId = location.getId().value();
        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setName("Warehouse A Updated");
        request.setDescription("Updated Description");

        when(locationRepository.findById(any(LocationId.class))).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        // Act
        Location updated = locationManagementService.updateLocation(locationId, request);

        // Assert
        assertEquals("Warehouse A Updated", updated.getName());
        assertEquals("Updated Description", updated.getDescription());
    }

    @Test
    void shouldGetLocationsPaged() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> locationPage = new PageImpl<>(List.of(location));
        when(locationRepository.findAll(pageable)).thenReturn(locationPage);

        // Act
        Page<Location> result = locationManagementService.getLocations(null, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
    }
}
