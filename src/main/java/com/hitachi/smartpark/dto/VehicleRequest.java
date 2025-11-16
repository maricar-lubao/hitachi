package com.hitachi.smartpark.dto;

import com.hitachi.smartpark.entity.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequest {
    
    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "License plate can only contain letters, numbers, and dashes")
    private String licensePlate;
    
    @NotNull(message = "Vehicle type is required")
    private VehicleType type;
    
    @NotBlank(message = "Owner name is required")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Owner name can only contain letters and spaces")
    private String ownerName;
}

