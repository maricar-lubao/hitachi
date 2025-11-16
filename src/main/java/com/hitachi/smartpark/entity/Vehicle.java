package com.hitachi.smartpark.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @Column(name = "license_plate", length = 50)
    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "License plate can only contain letters, numbers, and dashes")
    private String licensePlate;

    @NotNull(message = "Vehicle type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @NotBlank(message = "Owner name is required")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Owner name can only contain letters and spaces")
    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_lot_id")
    private ParkingLot parkingLot;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Transient
    public boolean isParked() {
        return parkingLot != null && checkInTime != null && checkOutTime == null;
    }

    @Transient
    public long getParkedMinutes() {
        if (checkInTime == null) {
            return 0;
        }
        LocalDateTime endTime = checkOutTime != null ? checkOutTime : LocalDateTime.now();
        return java.time.Duration.between(checkInTime, endTime).toMinutes();
    }
}

