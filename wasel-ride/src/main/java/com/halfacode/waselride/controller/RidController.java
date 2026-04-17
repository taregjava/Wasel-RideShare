package com.halfacode.waselride.controller;

import com.halfacode.waselride.dto.RideRequest;
import com.halfacode.waselride.dto.RideResponse;
import jakarta.validation.Valid;
import com.halfacode.waselride.model.RidsStatus;
import com.halfacode.waselride.service.RideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RidController {

    private final RideService rideService;

    @PostMapping("/request")
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody RideRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.createRide(request));
    }


    @GetMapping("/{riderId}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable String riderId) {
        return ResponseEntity.ok(rideService.getRideById(riderId));
    }
    @GetMapping("/rider/{riderId}")
    public ResponseEntity<List<RideResponse>> getRideByRiderId(@PathVariable String riderId) {
        return ResponseEntity.ok(rideService.getRidesByRider(riderId));
    }
    @PutMapping("/{riderId}/start")
    public ResponseEntity<RideResponse> startRide(@PathVariable String riderId) {
        return ResponseEntity.ok(rideService.startRide(riderId));
    }
    @PutMapping("/{riderId}/complete")
    public ResponseEntity<RideResponse> completeRide(@PathVariable String riderId) {
        return ResponseEntity.ok(rideService.completeRide(riderId));
    }

    @PutMapping("/{riderId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable String riderId) {
        return ResponseEntity.ok(rideService.cancelRide(riderId));
    }

}
