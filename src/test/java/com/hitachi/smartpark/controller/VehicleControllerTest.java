package com.hitachi.smartpark.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.smartpark.dto.CheckInRequest;
import com.hitachi.smartpark.dto.CheckOutResponse;
import com.hitachi.smartpark.dto.VehicleRequest;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.entity.VehicleType;
import com.hitachi.smartpark.service.VehicleService;
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
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@Import(TestSecurityConfig.class)
@DisplayName("Vehicle Controller Tests")
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    private Vehicle testVehicle;
    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        testVehicle = new Vehicle();
        testVehicle.setLicensePlate("ABC-123");
        testVehicle.setType(VehicleType.CAR);
        testVehicle.setOwnerName("John Doe");

        vehicleRequest = new VehicleRequest();
        vehicleRequest.setLicensePlate("ABC-123");
        vehicleRequest.setType(VehicleType.CAR);
        vehicleRequest.setOwnerName("John Doe");
    }

    @Test
    @WithMockUser
    @DisplayName("Should register vehicle successfully")
    void shouldRegisterVehicleSuccessfully() throws Exception {
        when(vehicleService.registerVehicle(any(VehicleRequest.class))).thenReturn(testVehicle);

        mockMvc.perform(post("/api/v1/vehicles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("ABC-123"))
                .andExpect(jsonPath("$.type").value("CAR"))
                .andExpect(jsonPath("$.ownerName").value("John Doe"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should check in vehicle successfully")
    void shouldCheckInVehicleSuccessfully() throws Exception {
        CheckInRequest request = new CheckInRequest("ABC-123", "LOT-001");
        when(vehicleService.checkIn(any(CheckInRequest.class))).thenReturn(testVehicle);

        mockMvc.perform(post("/api/v1/vehicles/check-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("ABC-123"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should check out vehicle successfully")
    void shouldCheckOutVehicleSuccessfully() throws Exception {
        CheckOutResponse response = new CheckOutResponse(
                "ABC-123", "LOT-001",
                LocalDateTime.now().minusMinutes(10),
                LocalDateTime.now(),
                10, new BigDecimal("5.00")
        );
        when(vehicleService.checkOut("ABC-123")).thenReturn(response);

        mockMvc.perform(post("/api/v1/vehicles/ABC-123/check-out")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("ABC-123"))
                .andExpect(jsonPath("$.minutesParked").value(10))
                .andExpect(jsonPath("$.parkingCost").value(5.00));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get vehicle by license plate successfully")
    void shouldGetVehicleByLicensePlateSuccessfully() throws Exception {
        when(vehicleService.getVehicle("ABC-123")).thenReturn(testVehicle);

        mockMvc.perform(get("/api/v1/vehicles/ABC-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("ABC-123"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get all vehicles successfully")
    void shouldGetAllVehiclesSuccessfully() throws Exception {
        when(vehicleService.getAllVehicles()).thenReturn(Arrays.asList(testVehicle));

        mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].licensePlate").value("ABC-123"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when license plate has invalid characters")
    void shouldReturn400WhenLicensePlateHasInvalidCharacters() throws Exception {
        vehicleRequest.setLicensePlate("ABC@123");

        mockMvc.perform(post("/api/v1/vehicles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should return 400 when owner name has numbers")
    void shouldReturn400WhenOwnerNameHasNumbers() throws Exception {
        vehicleRequest.setOwnerName("John123");

        mockMvc.perform(post("/api/v1/vehicles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isBadRequest());
    }
}

