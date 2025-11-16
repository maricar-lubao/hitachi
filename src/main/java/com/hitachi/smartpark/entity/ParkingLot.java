package com.hitachi.smartpark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_lots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLot {

    @Id
    @Column(name = "lot_id", length = 50)
    @NotBlank(message = "Lot ID is required")
    @Size(max = 50, message = "Lot ID must not exceed 50 characters")
    private String lotId;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity;

    @NotNull(message = "Occupied spaces is required")
    @Min(value = 0, message = "Occupied spaces cannot be negative")
    @Column(name = "occupied_spaces", nullable = false)
    private Integer occupiedSpaces = 0;

    @NotNull(message = "Cost per minute is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost per minute must be greater than 0")
    @Column(name = "cost_per_minute", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPerMinute;

    @OneToMany(mappedBy = "parkingLot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    public boolean isFull() {
        return occupiedSpaces >= capacity;
    }

    public int getAvailableSpaces() {
        return capacity - occupiedSpaces;
    }

    public void incrementOccupiedSpaces() {
        if (occupiedSpaces < capacity) {
            occupiedSpaces++;
        }
    }

    public void decrementOccupiedSpaces() {
        if (occupiedSpaces > 0) {
            occupiedSpaces--;
        }
    }
}

