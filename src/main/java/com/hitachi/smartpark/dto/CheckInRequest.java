package com.hitachi.smartpark.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRequest {
    
    @NotBlank(message = "License plate is required")
    private String licensePlate;
    
    @NotBlank(message = "Lot ID is required")
    private String lotId;
}

