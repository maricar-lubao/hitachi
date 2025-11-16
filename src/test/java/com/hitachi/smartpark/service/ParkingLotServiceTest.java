package com.hitachi.smartpark.service;

import com.hitachi.smartpark.dto.ParkingLotRequest;
import com.hitachi.smartpark.dto.ParkingLotStatusResponse;
import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.entity.Vehicle;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ParkingLot Service Tests")
class ParkingLotServiceTest {

    @Mock
    private ParkingLotRepository parkingLotRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private ParkingLotService parkingLotService;

    private ParkingLot testParkingLot;
    private ParkingLotRequest testRequest;

    @BeforeEach
    void setUp() {
        testParkingLot = new ParkingLot();
        testParkingLot.setLotId("LOT-001");
        testParkingLot.setLocation("Test Location");
        testParkingLot.setCapacity(50);
        testParkingLot.setOccupiedSpaces(0);
        testParkingLot.setCostPerMinute(new BigDecimal("0.50"));

        testRequest = new ParkingLotRequest();
        testRequest.setLotId("LOT-001");
        testRequest.setLocation("Test Location");
        testRequest.setCapacity(50);
        testRequest.setCostPerMinute(new BigDecimal("0.50"));
    }

    @Test
    @DisplayName("Should register parking lot successfully")
    void shouldRegisterParkingLotSuccessfully() {
        when(parkingLotRepository.existsById("LOT-001")).thenReturn(false);
        when(parkingLotRepository.save(any(ParkingLot.class))).thenReturn(testParkingLot);

        ParkingLot result = parkingLotService.registerParkingLot(testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getLotId()).isEqualTo("LOT-001");
        assertThat(result.getLocation()).isEqualTo("Test Location");
        assertThat(result.getCapacity()).isEqualTo(50);
        assertThat(result.getOccupiedSpaces()).isEqualTo(0);
        verify(parkingLotRepository).save(any(ParkingLot.class));
    }

    @Test
    @DisplayName("Should throw exception when registering duplicate parking lot")
    void shouldThrowExceptionWhenRegisteringDuplicateParkingLot() {
        when(parkingLotRepository.existsById("LOT-001")).thenReturn(true);

        assertThatThrownBy(() -> parkingLotService.registerParkingLot(testRequest))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("already exists");

        verify(parkingLotRepository, never()).save(any(ParkingLot.class));
    }

    @Test
    @DisplayName("Should get parking lot by ID successfully")
    void shouldGetParkingLotByIdSuccessfully() {
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(testParkingLot));

        ParkingLot result = parkingLotService.getParkingLot("LOT-001");

        assertThat(result).isNotNull();
        assertThat(result.getLotId()).isEqualTo("LOT-001");
        verify(parkingLotRepository).findById("LOT-001");
    }

    @Test
    @DisplayName("Should throw exception when parking lot not found")
    void shouldThrowExceptionWhenParkingLotNotFound() {
        when(parkingLotRepository.findById("LOT-999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> parkingLotService.getParkingLot("LOT-999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    @DisplayName("Should get parking lot status successfully")
    void shouldGetParkingLotStatusSuccessfully() {
        testParkingLot.setOccupiedSpaces(10);
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(testParkingLot));

        ParkingLotStatusResponse result = parkingLotService.getParkingLotStatus("LOT-001");

        assertThat(result).isNotNull();
        assertThat(result.getLotId()).isEqualTo("LOT-001");
        assertThat(result.getCapacity()).isEqualTo(50);
        assertThat(result.getOccupiedSpaces()).isEqualTo(10);
        assertThat(result.getAvailableSpaces()).isEqualTo(40);
    }

    @Test
    @DisplayName("Should get all parking lots successfully")
    void shouldGetAllParkingLotsSuccessfully() {
        ParkingLot lot2 = new ParkingLot();
        lot2.setLotId("LOT-002");
        List<ParkingLot> lots = Arrays.asList(testParkingLot, lot2);

        when(parkingLotRepository.findAll()).thenReturn(lots);

        List<ParkingLot> result = parkingLotService.getAllParkingLots();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testParkingLot, lot2);
    }

    @Test
    @DisplayName("Should get vehicles in lot successfully")
    void shouldGetVehiclesInLotSuccessfully() {
        when(parkingLotRepository.findById("LOT-001")).thenReturn(Optional.of(testParkingLot));
        when(vehicleRepository.findByParkingLotLotId("LOT-001")).thenReturn(Arrays.asList(new Vehicle()));

        List<Vehicle> result = parkingLotService.getVehiclesInLot("LOT-001");

        assertThat(result).hasSize(1);
        verify(vehicleRepository).findByParkingLotLotId("LOT-001");
    }
}

