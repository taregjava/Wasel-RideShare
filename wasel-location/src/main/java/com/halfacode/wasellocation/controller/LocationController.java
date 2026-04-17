package com.halfacode.wasellocation.controller;

import com.halfacode.wasellocation.dto.DriveLocationRequest;
import com.halfacode.wasellocation.dto.NearByDriveResponse;
import com.halfacode.wasellocation.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/drivers/update-location")
    public ResponseEntity<String> updateDriverLocation(
            @RequestBody DriveLocationRequest driveLocationRequest) {

        locationService.updateDriverLocation(driveLocationRequest);

        return ResponseEntity.ok("Driver location updated successfully");
    }

    @GetMapping("/drivers/nearby")
    public ResponseEntity<List<NearByDriveResponse>> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam (defaultValue = "5.0") double radiusKm
    ){
        return ResponseEntity.ok(locationService.getNearbyDrivers(latitude, longitude, radiusKm));
    }

    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(
            @RequestParam String driverId
    ){
        locationService.deleteDriverLocation(driverId);
        return ResponseEntity.ok("Driver location deleted successfully");
    }
}
