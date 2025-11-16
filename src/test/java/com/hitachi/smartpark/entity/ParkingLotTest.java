package com.hitachi.smartpark.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ParkingLot Entity Tests")
class ParkingLotTest {

    private ParkingLot parkingLot;

    @BeforeEach
    void setUp() {
        parkingLot = new ParkingLot();
        parkingLot.setLotId("LOT-001");
        parkingLot.setLocation("Test Location");
        parkingLot.setCapacity(50);
        parkingLot.setOccupiedSpaces(0);
        parkingLot.setCostPerMinute(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("Should calculate available spaces correctly")
    void shouldCalculateAvailableSpacesCorrectly() {
        parkingLot.setOccupiedSpaces(20);

        int availableSpaces = parkingLot.getAvailableSpaces();

        assertThat(availableSpaces).isEqualTo(30);
    }

    @Test
    @DisplayName("Should identify when parking lot is full")
    void shouldIdentifyWhenParkingLotIsFull() {
        parkingLot.setOccupiedSpaces(50);

        assertThat(parkingLot.isFull()).isTrue();
        assertThat(parkingLot.getAvailableSpaces()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should identify when parking lot is not full")
    void shouldIdentifyWhenParkingLotIsNotFull() {
        parkingLot.setOccupiedSpaces(30);

        assertThat(parkingLot.isFull()).isFalse();
        assertThat(parkingLot.getAvailableSpaces()).isEqualTo(20);
    }

    @Test
    @DisplayName("Should increment occupied spaces")
    void shouldIncrementOccupiedSpaces() {
        parkingLot.setOccupiedSpaces(10);

        parkingLot.incrementOccupiedSpaces();

        assertThat(parkingLot.getOccupiedSpaces()).isEqualTo(11);
    }

    @Test
    @DisplayName("Should not increment occupied spaces beyond capacity")
    void shouldNotIncrementOccupiedSpacesBeyondCapacity() {
        parkingLot.setOccupiedSpaces(50);

        parkingLot.incrementOccupiedSpaces();

        assertThat(parkingLot.getOccupiedSpaces()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should decrement occupied spaces")
    void shouldDecrementOccupiedSpaces() {
        parkingLot.setOccupiedSpaces(10);

        parkingLot.decrementOccupiedSpaces();

        assertThat(parkingLot.getOccupiedSpaces()).isEqualTo(9);
    }

    @Test
    @DisplayName("Should not decrement occupied spaces below zero")
    void shouldNotDecrementOccupiedSpacesBelowZero() {
        parkingLot.setOccupiedSpaces(0);

        parkingLot.decrementOccupiedSpaces();

        assertThat(parkingLot.getOccupiedSpaces()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should have all available spaces when empty")
    void shouldHaveAllAvailableSpacesWhenEmpty() {
        assertThat(parkingLot.getAvailableSpaces()).isEqualTo(50);
        assertThat(parkingLot.isFull()).isFalse();
    }
}

