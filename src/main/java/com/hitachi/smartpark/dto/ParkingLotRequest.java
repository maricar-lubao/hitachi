package com.hitachi.smartpark.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotRequest {
    
    @NotBlank(message = "Lot ID is required")
    @Size(max = 50, message = "Lot ID must not exceed 50 characters")
    private String lotId;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    @NotNull(message = "Cost per minute is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Cost per minute must be greater than 0")
    private BigDecimal costPerMinute;
}

