package com.halfacode.waselmatchin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriveResponse {

    private String driverId;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
