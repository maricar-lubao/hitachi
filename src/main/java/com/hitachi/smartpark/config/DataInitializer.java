package com.hitachi.smartpark.config;

import com.hitachi.smartpark.entity.ParkingLot;
import com.hitachi.smartpark.entity.Vehicle;
import com.hitachi.smartpark.entity.VehicleType;
import com.hitachi.smartpark.repository.ParkingLotRepository;
import com.hitachi.smartpark.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private ParkingLotRepository parkingLotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing data...");

        ParkingLot lot1 = new ParkingLot();
        lot1.setLotId("LOT-001");
        lot1.setLocation("Downtown Plaza");
        lot1.setCapacity(50);
        lot1.setOccupiedSpaces(0);
        lot1.setCostPerMinute(new BigDecimal("0.50"));
        parkingLotRepository.save(lot1);

        ParkingLot lot2 = new ParkingLot();
        lot2.setLotId("LOT-002");
        lot2.setLocation("Shopping Mall");
        lot2.setCapacity(100);
        lot2.setOccupiedSpaces(0);
        lot2.setCostPerMinute(new BigDecimal("0.75"));
        parkingLotRepository.save(lot2);

        ParkingLot lot3 = new ParkingLot();
        lot3.setLotId("LOT-003");
        lot3.setLocation("Airport Terminal");
        lot3.setCapacity(200);
        lot3.setOccupiedSpaces(0);
        lot3.setCostPerMinute(new BigDecimal("1.00"));
        parkingLotRepository.save(lot3);

        Vehicle vehicle1 = new Vehicle();
        vehicle1.setLicensePlate("ABC-123");
        vehicle1.setType(VehicleType.CAR);
        vehicle1.setOwnerName("John Doe");
        vehicleRepository.save(vehicle1);

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setLicensePlate("XYZ-789");
        vehicle2.setType(VehicleType.MOTORCYCLE);
        vehicle2.setOwnerName("Jane Smith");
        vehicleRepository.save(vehicle2);

        Vehicle vehicle3 = new Vehicle();
        vehicle3.setLicensePlate("TRK-456");
        vehicle3.setType(VehicleType.TRUCK);
        vehicle3.setOwnerName("Bob Johnson");
        vehicleRepository.save(vehicle3);

        Vehicle vehicle4 = new Vehicle();
        vehicle4.setLicensePlate("CAR-001");
        vehicle4.setType(VehicleType.CAR);
        vehicle4.setOwnerName("Alice Williams");
        vehicleRepository.save(vehicle4);

        Vehicle vehicle5 = new Vehicle();
        vehicle5.setLicensePlate("MOTO-999");
        vehicle5.setType(VehicleType.MOTORCYCLE);
        vehicle5.setOwnerName("Charlie Brown");
        vehicleRepository.save(vehicle5);

        logger.info("Data initialization completed successfully!");
        logger.info("Created {} parking lots", parkingLotRepository.count());
        logger.info("Created {} vehicles", vehicleRepository.count());
    }
}

