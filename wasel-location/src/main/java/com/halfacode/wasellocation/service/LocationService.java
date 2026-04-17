package com.halfacode.wasellocation.service;

import com.halfacode.wasellocation.dto.DriveLocationRequest;
import com.halfacode.wasellocation.dto.NearByDriveResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    public static final String DRIVER_GEO_KEY="drivers:locations";

    public void updateDriverLocation(DriveLocationRequest driveLocationRequest) {
    }

    public @Nullable List<NearByDriveResponse> getNearbyDrivers(double latitude, double longitude, double radiusKm) {
        return null;
    }

    public void deleteDriverLocation(String driverId) {
    }
}
