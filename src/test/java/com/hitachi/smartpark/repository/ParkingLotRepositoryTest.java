package com.hitachi.smartpark.repository;

import com.hitachi.smartpark.entity.ParkingLot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("ParkingLot Repository Tests")
class ParkingLotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    private ParkingLot testParkingLot;

    @BeforeEach
    void setUp() {
        testParkingLot = new ParkingLot();
        testParkingLot.setLotId("LOT-TEST");
        testParkingLot.setLocation("Test Location");
        testParkingLot.setCapacity(50);
        testParkingLot.setOccupiedSpaces(0);
        testParkingLot.setCostPerMinute(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("Should save and find parking lot by ID")
    void shouldSaveAndFindParkingLotById() {
        parkingLotRepository.save(testParkingLot);
        entityManager.flush();

        ParkingLot found = parkingLotRepository.findById("LOT-TEST").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getLotId()).isEqualTo("LOT-TEST");
        assertThat(found.getLocation()).isEqualTo("Test Location");
        assertThat(found.getCapacity()).isEqualTo(50);
        assertThat(found.getOccupiedSpaces()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should find all parking lots")
    void shouldFindAllParkingLots() {
        parkingLotRepository.save(testParkingLot);

        ParkingLot lot2 = new ParkingLot();
        lot2.setLotId("LOT-TEST-2");
        lot2.setLocation("Test Location 2");
        lot2.setCapacity(100);
        lot2.setOccupiedSpaces(0);
        lot2.setCostPerMinute(new BigDecimal("0.75"));
        parkingLotRepository.save(lot2);

        entityManager.flush();

        List<ParkingLot> lots = parkingLotRepository.findAll();

        assertThat(lots).hasSize(2);
    }

    @Test
    @DisplayName("Should update parking lot occupied spaces")
    void shouldUpdateParkingLotOccupiedSpaces() {
        parkingLotRepository.save(testParkingLot);
        entityManager.flush();

        testParkingLot.setOccupiedSpaces(10);
        parkingLotRepository.save(testParkingLot);
        entityManager.flush();

        ParkingLot found = parkingLotRepository.findById("LOT-TEST").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getOccupiedSpaces()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should delete parking lot")
    void shouldDeleteParkingLot() {
        parkingLotRepository.save(testParkingLot);
        entityManager.flush();

        parkingLotRepository.deleteById("LOT-TEST");
        entityManager.flush();

        var found = parkingLotRepository.findById("LOT-TEST");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should check if parking lot exists")
    void shouldCheckIfParkingLotExists() {
        parkingLotRepository.save(testParkingLot);
        entityManager.flush();

        boolean exists = parkingLotRepository.existsById("LOT-TEST");
        boolean notExists = parkingLotRepository.existsById("LOT-NONEXISTENT");

        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should calculate available spaces correctly")
    void shouldCalculateAvailableSpacesCorrectly() {
        testParkingLot.setOccupiedSpaces(20);
        parkingLotRepository.save(testParkingLot);
        entityManager.flush();

        ParkingLot found = parkingLotRepository.findById("LOT-TEST").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getAvailableSpaces()).isEqualTo(30);
        assertThat(found.isFull()).isFalse();
    }

    @Test
    @DisplayName("Should detect when parking lot is full")
    void shouldDetectWhenParkingLotIsFull() {
        testParkingLot.setOccupiedSpaces(50);
        parkingLotRepository.save(testParkingLot);
        entityManager.flush();

        ParkingLot found = parkingLotRepository.findById("LOT-TEST").orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.isFull()).isTrue();
        assertThat(found.getAvailableSpaces()).isEqualTo(0);
    }
}

