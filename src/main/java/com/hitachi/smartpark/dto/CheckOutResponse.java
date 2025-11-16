package com.hitachi.smartpark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {
    private String licensePlate;
    private String lotId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private long minutesParked;
    private BigDecimal parkingCost;
}

