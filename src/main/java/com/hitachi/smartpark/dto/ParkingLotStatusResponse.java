package com.hitachi.smartpark.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotStatusResponse {
    private String lotId;
    private String location;
    private int capacity;
    private int occupiedSpaces;
    private int availableSpaces;
}

