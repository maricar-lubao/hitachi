package com.hitachi.smartpark.service;

import com.hitachi.smartpark.dto.CheckInRequest;
import com.hitachi.smartpark.dto.CheckOutResponse;
import com.hitachi.smartpark.dto.VehicleRequest;
import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.exception.BusinessException;
import com.hitachi.smartpark.exception.ResourceAlreadyExistsException;
import com.hitachi.smartpark.exception.ResourceNotFoundException;
import com.hitachi.smartpark.repository.ParkingLotRepository;
import com.hitachi.smartpark.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Transactional
    public Vehicle registerVehicle(VehicleRequest request) {
        if (vehicleRepository.existsById(request.getLicensePlate())) {
            throw new ResourceAlreadyExistsException("Vehicle with license plate " + request.getLicensePlate() + " already exists");
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setType(request.getType());
        vehicle.setOwnerName(request.getOwnerName());

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public Vehicle checkIn(CheckInRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getLicensePlate())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with license plate: " + request.getLicensePlate()));

        if (vehicle.isParked()) {
            throw new BusinessException("Vehicle is already parked in lot: " + vehicle.getParkingLot().getLotId());
        }

        ParkingLot parkingLot = parkingLotRepository.findById(request.getLotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with ID: " + request.getLotId()));

        if (parkingLot.isFull()) {
            throw new BusinessException("Parking lot is full");
        }

        vehicle.setParkingLot(parkingLot);
        vehicle.setCheckInTime(LocalDateTime.now());
        vehicle.setCheckOutTime(null);

        parkingLot.incrementOccupiedSpaces();
        parkingLotRepository.save(parkingLot);

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    public CheckOutResponse checkOut(String licensePlate) {
        Vehicle vehicle = vehicleRepository.findById(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with license plate: " + licensePlate));

        if (!vehicle.isParked()) {
            throw new BusinessException("Vehicle is not currently parked");
        }

        ParkingLot parkingLot = vehicle.getParkingLot();
        LocalDateTime checkOutTime = LocalDateTime.now();
        vehicle.setCheckOutTime(checkOutTime);

        long minutesParked = vehicle.getParkedMinutes();
        BigDecimal parkingCost = parkingLot.getCostPerMinute().multiply(BigDecimal.valueOf(minutesParked));

        CheckOutResponse response = new CheckOutResponse(
                vehicle.getLicensePlate(),
                parkingLot.getLotId(),
                vehicle.getCheckInTime(),
                checkOutTime,
                minutesParked,
                parkingCost
        );

        vehicle.setParkingLot(null);
        vehicle.setCheckInTime(null);
        vehicle.setCheckOutTime(null);

        parkingLot.decrementOccupiedSpaces();
        parkingLotRepository.save(parkingLot);
        vehicleRepository.save(vehicle);

        return response;
    }

    public Vehicle getVehicle(String licensePlate) {
        return vehicleRepository.findById(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with license plate: " + licensePlate));
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    @Transactional
    public void removeVehiclesParkedLongerThan(int minutes) {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(minutes);
        List<Vehicle> vehicles = vehicleRepository.findVehiclesParkedLongerThan(cutoffTime);

        for (Vehicle vehicle : vehicles) {
            ParkingLot parkingLot = vehicle.getParkingLot();
            vehicle.setParkingLot(null);
            vehicle.setCheckInTime(null);
            vehicle.setCheckOutTime(null);

            parkingLot.decrementOccupiedSpaces();
            parkingLotRepository.save(parkingLot);
            vehicleRepository.save(vehicle);
        }
    }
}

