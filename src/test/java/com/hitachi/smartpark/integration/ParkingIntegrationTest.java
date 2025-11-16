package com.hitachi.smartpark.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.smartpark.dto.*;
import com.hitachi.smartpark.entity.VehicleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Parking Integration Tests")
class ParkingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should complete full parking workflow: login, register, check-in, check-out")
    void shouldCompleteFullParkingWorkflow() throws Exception {
        AuthRequest authRequest = new AuthRequest("admin", "admin123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        String token = authResponse.getToken();

        ParkingLotRequest lotRequest = new ParkingLotRequest();
        lotRequest.setLotId("INT-LOT-001");
        lotRequest.setLocation("Integration Test Location");
        lotRequest.setCapacity(10);
        lotRequest.setCostPerMinute(new BigDecimal("1.00"));

        mockMvc.perform(post("/api/v1/parking-lots")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lotRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.lotId").value("INT-LOT-001"));

        VehicleRequest vehicleRequest = new VehicleRequest();
        vehicleRequest.setLicensePlate("INT-VEH-001");
        vehicleRequest.setType(VehicleType.CAR);
        vehicleRequest.setOwnerName("Integration Test User");

        mockMvc.perform(post("/api/v1/vehicles")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("INT-VEH-001"));

        mockMvc.perform(get("/api/v1/parking-lots/INT-LOT-001/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupiedSpaces").value(0))
                .andExpect(jsonPath("$.availableSpaces").value(10));

        CheckInRequest checkInRequest = new CheckInRequest();
        checkInRequest.setLicensePlate("INT-VEH-001");
        checkInRequest.setLotId("INT-LOT-001");

        mockMvc.perform(post("/api/v1/vehicles/check-in")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkInRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("INT-VEH-001"));

        mockMvc.perform(get("/api/v1/parking-lots/INT-LOT-001/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupiedSpaces").value(1))
                .andExpect(jsonPath("$.availableSpaces").value(9));

        Thread.sleep(2000);

        mockMvc.perform(post("/api/v1/vehicles/INT-VEH-001/check-out")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("INT-VEH-001"))
                .andExpect(jsonPath("$.parkingCost").exists())
                .andExpect(jsonPath("$.minutesParked").exists());

        mockMvc.perform(get("/api/v1/parking-lots/INT-LOT-001/status")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.occupiedSpaces").value(0))
                .andExpect(jsonPath("$.availableSpaces").value(10));
    }

    @Test
    @DisplayName("Should prevent check-in to full parking lot")
    void shouldPreventCheckInToFullParkingLot() throws Exception {
        AuthRequest authRequest = new AuthRequest("admin", "admin123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        String token = authResponse.getToken();

        ParkingLotRequest lotRequest = new ParkingLotRequest();
        lotRequest.setLotId("FULL-LOT");
        lotRequest.setLocation("Full Lot Test");
        lotRequest.setCapacity(1);
        lotRequest.setCostPerMinute(new BigDecimal("1.00"));

        mockMvc.perform(post("/api/v1/parking-lots")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lotRequest)))
                .andExpect(status().isCreated());

        CheckInRequest checkIn1 = new CheckInRequest("ABC-123", "FULL-LOT");
        mockMvc.perform(post("/api/v1/vehicles/check-in")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkIn1)))
                .andExpect(status().isOk());

        CheckInRequest checkIn2 = new CheckInRequest("XYZ-789", "FULL-LOT");
        mockMvc.perform(post("/api/v1/vehicles/check-in")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(checkIn2)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Parking lot is full"));
    }
}

