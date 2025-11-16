package com.hitachi.smartpark.repository;

import com.hitachi.smartpark.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    
    List<Vehicle> findByParkingLotLotId(String lotId);
    
    Optional<Vehicle> findByLicensePlateAndParkingLotIsNotNull(String licensePlate);
    
    @Query("SELECT v FROM Vehicle v WHERE v.parkingLot IS NOT NULL AND v.checkInTime IS NOT NULL " +
           "AND v.checkOutTime IS NULL AND v.checkInTime < :cutoffTime")
    List<Vehicle> findVehiclesParkedLongerThan(LocalDateTime cutoffTime);
}

