package com.halfacode.wasellocation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriveResponse {
    private String driveId;
    private double latitude;
    private double longitude;
    private double distanceInKm;


}
