package com.halfacode.waselride.dto;

import com.halfacode.waselride.model.RidsStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequest {

    @NotBlank(message = "riderId is required")
    private String riderId;

    @DecimalMin(value = "-90.0", message = "pickupLatitude must be >= -90")
    @DecimalMax(value = "90.0", message = "pickupLatitude must be <= 90")
    private double pickupLatitude;

    @DecimalMin(value = "-180.0", message = "pickupLongitude must be >= -180")
    @DecimalMax(value = "180.0", message = "pickupLongitude must be <= 180")
    private double pickupLongitude;

    @NotBlank(message = "pickupAddress is required")
    private String pickupAddress;

    @DecimalMin(value = "-90.0", message = "dropLatitude must be >= -90")
    @DecimalMax(value = "90.0", message = "dropLatitude must be <= 90")
    private double dropLatitude;

    @DecimalMin(value = "-180.0", message = "dropLongitude must be >= -180")
    @DecimalMax(value = "180.0", message = "dropLongitude must be <= 180")
    private double dropLongitude;

    @NotBlank(message = "dropAddress is required")
    private String dropAddress;
    private RidsStatus status;
    private double estimatedFare;
    private double actualFare;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

}

