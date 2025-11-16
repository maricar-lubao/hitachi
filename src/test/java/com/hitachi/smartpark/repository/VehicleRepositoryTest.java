package com.hitachi.smartpark.repository;

import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.entity.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Vehicle Repository Tests")
class VehicleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    private ParkingLot testParkingLot;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        testParkingLot = new ParkingLot();
        testParkingLot.setLotId("LOT-TEST");
        testParkingLot.setLocation("Test Location");
        testParkingLot.setCapacity(50);
        testParkingLot.setOccupiedSpaces(0);
        testParkingLot.setCostPerMinute(new BigDecimal("0.50"));
        testParkingLot = entityManager.persist(testParkingLot);

        testVehicle = new Vehicle();
        testVehicle.setLicensePlate("TEST-123");
        testVehicle.setType(VehicleType.CAR);
        testVehicle.setOwnerName("Test Owner");
    }

    @Test
    @DisplayName("Should save and find vehicle by license plate")
    void shouldSaveAndFindVehicleByLicensePlate() {
        Vehicle saved = vehicleRepository.save(testVehicle);
        entityManager.flush();

        Vehicle found = vehicleRepository.findById("TEST-123").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getLicensePlate()).isEqualTo("TEST-123");
        assertThat(found.getType()).isEqualTo(VehicleType.CAR);
        assertThat(found.getOwnerName()).isEqualTo("Test Owner");
    }

    @Test
    @DisplayName("Should find vehicles by parking lot ID")
    void shouldFindVehiclesByParkingLotId() {
        testVehicle.setParkingLot(testParkingLot);
        testVehicle.setCheckInTime(LocalDateTime.now());
        vehicleRepository.save(testVehicle);
        entityManager.flush();

        List<Vehicle> vehicles = vehicleRepository.findByParkingLotLotId("LOT-TEST");

        assertThat(vehicles).hasSize(1);
        assertThat(vehicles.get(0).getLicensePlate()).isEqualTo("TEST-123");
    }

    @Test
    @DisplayName("Should find vehicles parked longer than specified time")
    void shouldFindVehiclesParkedLongerThanSpecifiedTime() {
        testVehicle.setParkingLot(testParkingLot);
        testVehicle.setCheckInTime(LocalDateTime.now().minusMinutes(20));
        vehicleRepository.save(testVehicle);
        entityManager.flush();

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<Vehicle> vehicles = vehicleRepository.findVehiclesParkedLongerThan(cutoffTime);

        assertThat(vehicles).hasSize(1);
        assertThat(vehicles.get(0).getLicensePlate()).isEqualTo("TEST-123");
    }

    @Test
    @DisplayName("Should not find vehicles parked less than specified time")
    void shouldNotFindVehiclesParkedLessThanSpecifiedTime() {
        testVehicle.setParkingLot(testParkingLot);
        testVehicle.setCheckInTime(LocalDateTime.now().minusMinutes(10));
        vehicleRepository.save(testVehicle);
        entityManager.flush();

        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(15);
        List<Vehicle> vehicles = vehicleRepository.findVehiclesParkedLongerThan(cutoffTime);

        assertThat(vehicles).isEmpty();
    }

    @Test
    @DisplayName("Should find vehicle by license plate when parked")
    void shouldFindVehicleByLicensePlateWhenParked() {
        testVehicle.setParkingLot(testParkingLot);
        testVehicle.setCheckInTime(LocalDateTime.now());
        vehicleRepository.save(testVehicle);
        entityManager.flush();

        var found = vehicleRepository.findByLicensePlateAndParkingLotIsNotNull("TEST-123");

        assertThat(found).isPresent();
        assertThat(found.get().getLicensePlate()).isEqualTo("TEST-123");
    }

    @Test
    @DisplayName("Should not find vehicle by license plate when not parked")
    void shouldNotFindVehicleByLicensePlateWhenNotParked() {
        vehicleRepository.save(testVehicle);
        entityManager.flush();

        var found = vehicleRepository.findByLicensePlateAndParkingLotIsNotNull("TEST-123");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should delete vehicle")
    void shouldDeleteVehicle() {
        vehicleRepository.save(testVehicle);
        entityManager.flush();

        vehicleRepository.deleteById("TEST-123");
        entityManager.flush();

        var found = vehicleRepository.findById("TEST-123");
        assertThat(found).isEmpty();
    }
}

