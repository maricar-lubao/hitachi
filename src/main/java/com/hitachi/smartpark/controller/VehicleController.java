package com.hitachi.smartpark.controller;

import com.hitachi.smartpark.dto.CheckInRequest;
import com.hitachi.smartpark.dto.CheckOutResponse;
import com.hitachi.smartpark.dto.VehicleRequest;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> registerVehicle(@Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = vehicleService.registerVehicle(request);
        return new ResponseEntity<>(vehicle, HttpStatus.CREATED);
    }

    @PostMapping("/check-in")
    public ResponseEntity<Vehicle> checkIn(@Valid @RequestBody CheckInRequest request) {
        Vehicle vehicle = vehicleService.checkIn(request);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping("/{licensePlate}/check-out")
    public ResponseEntity<CheckOutResponse> checkOut(@PathVariable String licensePlate) {
        CheckOutResponse response = vehicleService.checkOut(licensePlate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{licensePlate}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable String licensePlate) {
        Vehicle vehicle = vehicleService.getVehicle(licensePlate);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }
}

