package com.hitachi.smartpark.service;

import com.hitachi.smartpark.dto.ParkingLotRequest;
import com.hitachi.smartpark.dto.ParkingLotStatusResponse;
import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.exception.ResourceAlreadyExistsException;
import com.hitachi.smartpark.exception.ResourceNotFoundException;
import com.hitachi.smartpark.repository.ParkingLotRepository;
import com.hitachi.smartpark.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ParkingLotService {

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public ParkingLot registerParkingLot(ParkingLotRequest request) {
        if (parkingLotRepository.existsById(request.getLotId())) {
            throw new ResourceAlreadyExistsException("Parking lot with ID " + request.getLotId() + " already exists");
        }

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setLotId(request.getLotId());
        parkingLot.setLocation(request.getLocation());
        parkingLot.setCapacity(request.getCapacity());
        parkingLot.setOccupiedSpaces(0);
        parkingLot.setCostPerMinute(request.getCostPerMinute());

        return parkingLotRepository.save(parkingLot);
    }

    public ParkingLot getParkingLot(String lotId) {
        return parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found with ID: " + lotId));
    }

    public ParkingLotStatusResponse getParkingLotStatus(String lotId) {
        ParkingLot parkingLot = getParkingLot(lotId);
        return new ParkingLotStatusResponse(
                parkingLot.getLotId(),
                parkingLot.getLocation(),
                parkingLot.getCapacity(),
                parkingLot.getOccupiedSpaces(),
                parkingLot.getAvailableSpaces()
        );
    }

    public List<Vehicle> getVehiclesInLot(String lotId) {
        getParkingLot(lotId);
        return vehicleRepository.findByParkingLotLotId(lotId);
    }

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }
}

