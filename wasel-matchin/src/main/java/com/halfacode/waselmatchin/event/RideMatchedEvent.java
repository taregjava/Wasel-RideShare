package com.halfacode.waselmatchin.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideMatchedEvent {

    private String rideId;
    private String riderId;
    private String driverId;
    private double latitude;
    private double longitude;
    private Double distanceToPickup;


}
