package com.stockmanagement.inventory.presentation.dto;

import com.stockmanagement.inventory.domain.model.Location;
import com.stockmanagement.inventory.domain.model.Location.LocationType;
import com.stockmanagement.inventory.domain.model.Location.LocationStatus;
import lombok.Data;

import java.time.Instant;

@Data
public class LocationResponse {
    private String id;
    private String name;
    private LocationType type;
    private String description;
    private String address;
    private LocationStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public static LocationResponse fromDomain(Location location) {
        LocationResponse response = new LocationResponse();
        response.setId(location.getId().value());
        response.setName(location.getName());
        response.setType(location.getType());
        response.setDescription(location.getDescription());
        response.setAddress(location.getAddress());
        response.setStatus(location.getStatus());
        response.setCreatedAt(location.getCreatedAt());
        response.setUpdatedAt(location.getUpdatedAt());
        return response;
    }
}
