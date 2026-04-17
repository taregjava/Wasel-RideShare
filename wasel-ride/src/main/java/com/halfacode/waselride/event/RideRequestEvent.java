package com.halfacode.waselride.event;

import com.halfacode.waselride.model.RidsStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestEvent {

    private String rideId;
    private String riderId;
    private double pickupLatitude;
    private double pickupLongitude;
    private String pickupAddress;
    private double dropLatitude;
    private double dropLongitude;
    private String dropAddress;
    private RidsStatus status;
    private double estimatedFare;
    private LocalDateTime eventOccurredAt;
}

