package com.hitachi.smartpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.smartpark.dto.ParkingLotRequest;
import com.hitachi.smartpark.dto.ParkingLotStatusResponse;
import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.service.ParkingLotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ParkingLotController.class)
@Import(TestSecurityConfig.class)
@DisplayName("ParkingLot Controller Tests")
class ParkingLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
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
    @WithMockUser
    @DisplayName("Should register parking lot successfully")
    void shouldRegisterParkingLotSuccessfully() throws Exception {
        when(parkingLotService.registerParkingLot(any(ParkingLotRequest.class))).thenReturn(testParkingLot);

        mockMvc.perform(post("/api/v1/parking-lots")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lotId").value("LOT-001"))
                .andExpect(jsonPath("$.location").value("Test Location"))
                .andExpect(jsonPath("$.capacity").value(50));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get parking lot by ID successfully")
    void shouldGetParkingLotByIdSuccessfully() throws Exception {
        when(parkingLotService.getParkingLot("LOT-001")).thenReturn(testParkingLot);

        mockMvc.perform(get("/api/v1/parking-lots/LOT-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lotId").value("LOT-001"))
                .andExpect(jsonPath("$.location").value("Test Location"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get parking lot status successfully")
    void shouldGetParkingLotStatusSuccessfully() throws Exception {
        ParkingLotStatusResponse status = new ParkingLotStatusResponse("LOT-001", "Test Location", 50, 10, 40);
        when(parkingLotService.getParkingLotStatus("LOT-001")).thenReturn(status);

        mockMvc.perform(get("/api/v1/parking-lots/LOT-001/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lotId").value("LOT-001"))
                .andExpect(jsonPath("$.occupiedSpaces").value(10))
                .andExpect(jsonPath("$.availableSpaces").value(40));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all parking lots successfully")
    void shouldGetAllParkingLotsSuccessfully() throws Exception {
        List<ParkingLot> lots = Arrays.asList(testParkingLot);
        when(parkingLotService.getAllParkingLots()).thenReturn(lots);

        mockMvc.perform(get("/api/v1/parking-lots"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lotId").value("LOT-001"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when lot ID is blank")
    void shouldReturn400WhenLotIdIsBlank() throws Exception {
        testRequest.setLotId("");

        mockMvc.perform(post("/api/v1/parking-lots")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when capacity is negative")
    void shouldReturn400WhenCapacityIsNegative() throws Exception {
        testRequest.setCapacity(-1);

        mockMvc.perform(post("/api/v1/parking-lots")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testRequest)))
                .andExpect(status().isBadRequest());
    }
}

