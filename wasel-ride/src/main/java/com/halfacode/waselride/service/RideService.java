package com.halfacode.waselride.service;

import com.halfacode.waselride.dto.RideRequest;
import com.halfacode.waselride.dto.RideResponse;
import com.halfacode.waselride.event.RideRequestEvent;
import com.halfacode.waselride.model.Ride;
import com.halfacode.waselride.model.RidsStatus;
import com.halfacode.waselride.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParseException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RideService {

    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestEvent> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topics.ride-requested}")
    private String rideRequestedTopic;

    public RideResponse createRide(RideRequest request) {
        Ride ride = mapToEntity(request);
        ride.setStatus(RidsStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimate(request));

        Ride savedRide = rideRepository.save(ride);
        publishRideRequestedEvent(savedRide);

        savedRide.setStatus(RidsStatus.MATCHING);
        rideRepository.save(savedRide);
        return toResponse(savedRide);
    }

    public RideResponse getRideById(String id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + id));
        return toResponse(ride);
    }

    public List<RideResponse> getAllRides() {
        return rideRepository.findAll().stream().map(this::toResponse).toList();
    }

    public List<RideResponse> getRidesByRider(String riderId) {
        return rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<RideResponse> getRidesByStatus(RidsStatus status) {
        return rideRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    public RideResponse updateRideStatus(String id, RidsStatus status) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + id));
        ride.setStatus(status);
        return toResponse(rideRepository.save(ride));
    }

    public RideResponse updateRide(String id, RideRequest request) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + id));
        ride.setRiderId(request.getRiderId());
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropLatitude(request.getDropLatitude());
        ride.setDropLongitude(request.getDropLongitude());
        ride.setDropAddress(request.getDropAddress());
        ride.setEstimatedFare(request.getEstimatedFare());
        ride.setActualFare(request.getActualFare());
        ride.setStartedAt(request.getStartedAt());
        ride.setCompletedAt(request.getCompletedAt());
        if (request.getStatus() != null) {
            ride.setStatus(request.getStatus());
        }
        return toResponse(rideRepository.save(ride));
    }

    public void deleteRide(String id) {
        rideRepository.deleteById(id);
    }

    private Ride mapToEntity(RideRequest request) {
        Ride ride = new Ride();
        ride.setRiderId(request.getRiderId());
        ride.setPickupLatitude(request.getPickupLatitude());
        ride.setPickupLongitude(request.getPickupLongitude());
        ride.setPickupAddress(request.getPickupAddress());
        ride.setDropLatitude(request.getDropLatitude());
        ride.setDropLongitude(request.getDropLongitude());
        ride.setDropAddress(request.getDropAddress());
        return ride;
    }

    private double calculateEstimate(RideRequest request) {
        double lon1 = Math.toRadians(request.getPickupLongitude());
        double lat1 = Math.toRadians(request.getPickupLatitude());
        double lon2 = Math.toRadians(request.getDropLongitude());
        double lat2 = Math.toRadians(request.getDropLatitude());

        double a = Math.pow(Math.sin((lat2 - lat1) / 2), 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.pow(Math.sin((lon2 - lon1) / 2), 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double distance = 6371 * c; // Earth radius in kilometers

        double baseFare = 50 +(distance * 12); // Base fare in dollars
        return Math.round(baseFare * 100.0) / 100.0; // Round to 2 decimal places
    }

    private void publishRideRequestedEvent(Ride savedRide) {
        RideRequestEvent event = RideRequestEvent.builder()
                .rideId(savedRide.getId())
                .riderId(savedRide.getRiderId())
                .pickupLatitude(savedRide.getPickupLatitude())
                .pickupLongitude(savedRide.getPickupLongitude())
                .pickupAddress(savedRide.getPickupAddress())
                .dropLatitude(savedRide.getDropLatitude())
                .dropLongitude(savedRide.getDropLongitude())
                .dropAddress(savedRide.getDropAddress())
                .status(savedRide.getStatus())
                .estimatedFare(savedRide.getEstimatedFare())
                .eventOccurredAt(LocalDateTime.now())
                .build();


            kafkaTemplate.send(rideRequestedTopic, savedRide.getId(), event);
            log.info("Published RideRequestEvent for rideId: {}", savedRide.getId());

    }


    public void updateRideWithDriver(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        if (ride.getStatus() != RidsStatus.ACCEPTED) {
            throw new RuntimeException("Ride cannot be started. Current status: " + ride.getStatus());
        }
        ride.setStatus(RidsStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        rideRepository.save(ride);
    }
    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        if (ride.getStatus() != RidsStatus.ACCEPTED) {
            throw new RuntimeException("Ride cannot be started. Current status: " + ride.getStatus());
        }
        ride.setStatus(RidsStatus.RIDE_STARTED);
        ride.setStartedAt(LocalDateTime.now());
        return toResponse(rideRepository.save(ride));
    }
    public  RideResponse completeRide(String riderId) {
        Ride ride = rideRepository.findById(riderId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + riderId));
        if (ride.getStatus() != RidsStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride cannot be complete. Current status: " + ride.getStatus());
        }
        ride.setStatus(RidsStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getActualFare());
        return toResponse(rideRepository.save(ride));
    }

    public RideResponse cancelRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
        if (ride.getStatus() == RidsStatus.RIDE_STARTED || ride.getStatus() == RidsStatus.COMPLETED) {
            throw new RuntimeException("Ride cannot be cancelled. Current status: " + ride.getStatus());
        }
        ride.setStatus(RidsStatus.CANCELLED);
        return toResponse(rideRepository.save(ride));
    }
    private RideResponse toResponse(Ride ride) {
        return new RideResponse(
                ride.getId(),
                ride.getRiderId(),
                ride.getPickupLatitude(),
                ride.getPickupLongitude(),
                ride.getPickupAddress(),
                ride.getDropLatitude(),
                ride.getDropLongitude(),
                ride.getDropAddress(),
                ride.getStatus(),
                ride.getEstimatedFare(),
                ride.getActualFare(),
                ride.getCreatedAt(),
                ride.getUpdatedAt(),
                ride.getStartedAt(),
                ride.getCompletedAt()
        );
    }


}
