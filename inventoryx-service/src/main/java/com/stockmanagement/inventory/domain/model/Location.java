package com.stockmanagement.inventory.domain.model;

import com.stockmanagement.inventory.domain.model.valueobject.LocationId;

import java.time.Instant;

/**
 * Location - Aggregate Root for Warehouse/Store Locations.
 *
 * @author InventoryX Development Team
 * @since 2026-01-19
 */
public class Location {

    private final LocationId id;
    private String name;
    private LocationType type;
    private String description;
    private String address;
    private LocationStatus status;

    private final Instant createdAt;
    private Instant updatedAt;

    public enum LocationType {
        WAREHOUSE, STORE, TRANSIT
    }

    public enum LocationStatus {
        ACTIVE, INACTIVE
    }

    // Constructor
    public Location(LocationId id, String name, LocationType type, String description, String address,
            LocationStatus status, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.address = address;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method for new location
    public static Location create(String name, LocationType type, String description, String address) {
        Instant now = Instant.now();
        return new Location(
                new LocationId(java.util.UUID.randomUUID().toString()),
                name,
                type,
                description,
                address,
                LocationStatus.ACTIVE,
                now,
                now);
    }

    // Update location details
    public void updateDetails(String name, LocationType type, String description, String address,
            LocationStatus status) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.address = address;
        this.status = status;
        this.updatedAt = Instant.now();
    }

    // Getters
    public LocationId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocationType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public LocationStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
