package com.stockmanagement.inventory.presentation.rest;

import com.stockmanagement.inventory.application.service.LocationManagementService;
import com.stockmanagement.inventory.domain.model.Location;
import com.stockmanagement.inventory.presentation.dto.CreateLocationRequest;
import com.stockmanagement.inventory.presentation.dto.LocationResponse;
import com.stockmanagement.inventory.presentation.dto.UpdateLocationRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationManagementService locationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<Page<LocationResponse>> getLocations(
            @RequestParam(required = false) String search,
            @PageableDefault(sort = "name") Pageable pageable) {
        Page<LocationResponse> locations = locationService.getLocations(search, pageable)
                .map(LocationResponse::fromDomain);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<List<LocationResponse>> getActiveLocations() {
        List<LocationResponse> locations = locationService.getActiveLocations().stream()
                .map(LocationResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'USER')")
    public ResponseEntity<LocationResponse> getLocation(@PathVariable String id) {
        Location location = locationService.getLocation(id);
        return ResponseEntity.ok(LocationResponse.fromDomain(location));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody CreateLocationRequest request) {
        Location location = locationService.createLocation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(LocationResponse.fromDomain(location));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody UpdateLocationRequest request) {
        Location location = locationService.updateLocation(id, request);
        return ResponseEntity.ok(LocationResponse.fromDomain(location));
    }
}
