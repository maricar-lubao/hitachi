package com.hitachi.smartpark.controller;

import com.hitachi.smartpark.dto.ParkingLotRequest;
import com.hitachi.smartpark.dto.ParkingLotStatusResponse;
import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.service.ParkingLotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parking-lots")
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;

    @PostMapping
    public ResponseEntity<ParkingLot> registerParkingLot(@Valid @RequestBody ParkingLotRequest request) {
        ParkingLot parkingLot = parkingLotService.registerParkingLot(request);
        return new ResponseEntity<>(parkingLot, HttpStatus.CREATED);
    }

    @GetMapping("/{lotId}")
    public ResponseEntity<ParkingLot> getParkingLot(@PathVariable String lotId) {
        ParkingLot parkingLot = parkingLotService.getParkingLot(lotId);
        return ResponseEntity.ok(parkingLot);
    }

    @GetMapping("/{lotId}/status")
    public ResponseEntity<ParkingLotStatusResponse> getParkingLotStatus(@PathVariable String lotId) {
        ParkingLotStatusResponse status = parkingLotService.getParkingLotStatus(lotId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{lotId}/vehicles")
    public ResponseEntity<List<Vehicle>> getVehiclesInLot(@PathVariable String lotId) {
        List<Vehicle> vehicles = parkingLotService.getVehiclesInLot(lotId);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAllParkingLots() {
        List<ParkingLot> parkingLots = parkingLotService.getAllParkingLots();
        return ResponseEntity.ok(parkingLots);
    }
}

