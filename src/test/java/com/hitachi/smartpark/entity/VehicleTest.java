package com.hitachi.smartpark.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Vehicle Entity Tests")
class VehicleTest {

    private Vehicle vehicle;
    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        vehicle = new Vehicle();
        vehicle.setLicensePlate("TEST-123");
        vehicle.setType(VehicleType.CAR);
        vehicle.setOwnerName("Test Owner");

        parkingLot = new ParkingLot();
        parkingLot.setLotId("LOT-001");
        parkingLot.setLocation("Test Location");
        parkingLot.setCapacity(50);
        parkingLot.setOccupiedSpaces(0);
        parkingLot.setCostPerMinute(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("Should correctly identify parked vehicle")
    void shouldCorrectlyIdentifyParkedVehicle() {
        vehicle.setParkingLot(parkingLot);
        vehicle.setCheckInTime(LocalDateTime.now());

        assertThat(vehicle.isParked()).isTrue();
    }

    @Test
    @DisplayName("Should correctly identify non-parked vehicle")
    void shouldCorrectlyIdentifyNonParkedVehicle() {
        assertThat(vehicle.isParked()).isFalse();
    }

    @Test
    @DisplayName("Should calculate parked minutes correctly")
    void shouldCalculateParkedMinutesCorrectly() {
        vehicle.setParkingLot(parkingLot);
        vehicle.setCheckInTime(LocalDateTime.now().minusMinutes(30));

        long minutes = vehicle.getParkedMinutes();

        assertThat(minutes).isGreaterThanOrEqualTo(30);
        assertThat(minutes).isLessThan(31);
    }

    @Test
    @DisplayName("Should calculate parked minutes with checkout time")
    void shouldCalculateParkedMinutesWithCheckoutTime() {
        LocalDateTime checkIn = LocalDateTime.now().minusMinutes(60);
        LocalDateTime checkOut = LocalDateTime.now().minusMinutes(30);

        vehicle.setCheckInTime(checkIn);
        vehicle.setCheckOutTime(checkOut);

        long minutes = vehicle.getParkedMinutes();

        assertThat(minutes).isGreaterThanOrEqualTo(30);
        assertThat(minutes).isLessThan(31);
    }

    @Test
    @DisplayName("Should return zero minutes when not checked in")
    void shouldReturnZeroMinutesWhenNotCheckedIn() {
        long minutes = vehicle.getParkedMinutes();

        assertThat(minutes).isEqualTo(0);
    }

    @Test
    @DisplayName("Should not be parked when checked out")
    void shouldNotBeParkedWhenCheckedOut() {
        vehicle.setParkingLot(parkingLot);
        vehicle.setCheckInTime(LocalDateTime.now().minusMinutes(10));
        vehicle.setCheckOutTime(LocalDateTime.now());

        assertThat(vehicle.isParked()).isFalse();
    }
}

