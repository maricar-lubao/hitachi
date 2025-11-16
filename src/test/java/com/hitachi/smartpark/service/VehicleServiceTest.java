package com.hitachi.smartpark.service;

import com.hitachi.smartpark.dto.CheckInRequest;
import com.hitachi.smartpark.dto.CheckOutResponse;
import com.hitachi.smartpark.dto.VehicleRequest;
import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.entity.VehicleType;
import com.hitachi.smartpark.exception.BusinessException;
import com.hitachi.smartpark.exception.ResourceAlreadyExistsException;
import com.hitachi.smartpark.exception.ResourceNotFoundException;
import com.hitachi.smartpark.repository.ParkingLotRepository;
import com.hitachi.smartpark.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Vehicle Service Tests")
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private ParkingLot testParkingLot;
    private VehicleRequest vehicleRequest;
    private CheckInRequest checkInRequest;

    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle();
        testVehicle.setLicensePlate("ABC-123");
        testVehicle.setType(VehicleType.CAR);
        testVehicle.setOwnerName("John Doe");

        testParkingLot = new ParkingLot();
        testParkingLot.setLotId("LOT-001");
        testParkingLot.setLocation("Test Location");
        testParkingLot.setCapacity(50);
        testParkingLot.setOccupiedSpaces(0);
        testParkingLot.setCostPerMinute(new BigDecimal("0.50"));

        vehicleRequest = new VehicleRequest();
        vehicleRequest.setLicensePlate("ABC-123");
        vehicleRequest.setType(VehicleType.CAR);
        vehicleRequest.setOwnerName("John Doe");

        checkInRequest = new CheckInRequest();
        checkInRequest.setLicensePlate("ABC-123");
        checkInRequest.setLotId("LOT-001");
    }

    @Test
    @DisplayName("Should register vehicle successfully")
    void shouldRegisterVehicleSuccessfully() {
        when(vehicleRepository.existsById("ABC-123")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle result = vehicleService.registerVehicle(vehicleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getLicensePlate()).isEqualTo("ABC-123");
        assertThat(result.getType()).isEqualTo(VehicleType.CAR);
        assertThat(result.getOwnerName()).isEqualTo("John Doe");
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when registering duplicate vehicle")
    void shouldThrowExceptionWhenRegisteringDuplicateVehicle() {
        when(vehicleRepository.existsById("ABC-123")).thenReturn(true);

        assertThatThrownBy(() -> vehicleService.registerVehicle(vehicleRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should check in vehicle successfully")
    void shouldCheckInVehicleSuccessfully() {
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(testVehicle));
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(testParkingLot));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(testVehicle);

        Vehicle result = vehicleService.checkIn(checkInRequest);

        assertThat(result).isNotNull();
        verify(parkingLotRepository).save(testParkingLot);
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when checking in to full parking lot")
    void shouldThrowExceptionWhenCheckingInToFullParkingLot() {
        testParkingLot.setOccupiedSpaces(50);
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(testVehicle));
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(testParkingLot));

        assertThatThrownBy(() -> vehicleService.checkIn(checkInRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("full");

        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("Should throw exception when checking in already parked vehicle")
    void shouldThrowExceptionWhenCheckingInAlreadyParkedVehicle() {
        testVehicle.setParkingLot(testParkingLot);
        testVehicle.setCheckInTime(LocalDateTime.now());
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(testVehicle));

        assertThatThrownBy(() -> vehicleService.checkIn(checkInRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already parked");
    }

    @Test
    @DisplayName("Should throw exception when vehicle not found for check-in")
    void shouldThrowExceptionWhenVehicleNotFoundForCheckIn() {
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.checkIn(checkInRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should check out vehicle successfully and calculate cost")
    void shouldCheckOutVehicleSuccessfullyAndCalculateCost() {
        testVehicle.setParkingLot(testParkingLot);
        testVehicle.setCheckInTime(LocalDateTime.now().minusMinutes(10));
        when(vehicleRepository.findById("ABC-123")).thenReturn(Optional.of(testVehicle));

        CheckOutResponse result = vehicleService.checkOut("ABC-123");

        assertThat(result).isNotNull();
        assertThat(result.getLicensePlate()).isEqualTo("ABC-123");
        assertThat(result.getMinutesParked()).isGreaterThanOrEqualTo(10);
        assertThat(result.getParkingCost()).isGreaterThan(BigDecimal.ZERO);
        verify(parkingLotRepository).save(testParkingLot);
        verify(vehicleRepository).save(testVehicle);
    }
}

