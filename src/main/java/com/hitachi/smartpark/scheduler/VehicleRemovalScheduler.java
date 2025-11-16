package com.hitachi.smartpark.scheduler;

import com.hitachi.smartpark.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VehicleRemovalScheduler {

    private static final Logger logger = LoggerFactory.getLogger(VehicleRemovalScheduler.class);

    @Autowired
    private VehicleService vehicleService;

    @Scheduled(fixedRate = 60000)
    public void removeVehiclesParkedLongerThan15Minutes() {
        logger.info("Running scheduled task to remove vehicles parked longer than 15 minutes");
        try {
            vehicleService.removeVehiclesParkedLongerThan(15);
            logger.info("Completed scheduled task for vehicle removal");
        } catch (Exception e) {
            logger.error("Error during scheduled vehicle removal", e);
        }
    }
}

