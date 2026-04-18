package com.halfacode.waselmatchin.service;

import com.halfacode.waselmatchin.client.LocationServiceClient;
import com.halfacode.waselmatchin.dto.NearByDriveResponse;
import com.halfacode.waselmatchin.event.RideMatchedEvent;
import com.halfacode.waselmatchin.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchingService {

    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    private static final String TOPIC = "ride.matched";

    private static final double DEFAULT_SEARCH_RADIUS_KM = 5.0; // Default search radius for matching rides

    public void matchDriverRide(RideRequestedEvent rideRequestedEvent){

        List<NearByDriveResponse> nearbyDrivers = locationServiceClient.getNearbyDrivers(
                rideRequestedEvent.getPickupLatitude(),
                rideRequestedEvent.getPickupLongitude(),
                DEFAULT_SEARCH_RADIUS_KM
        );
        if (nearbyDrivers.isEmpty()) {
            log.info("No nearby drivers found for ride request: {}", rideRequestedEvent.getRideId());
            return;
        }
     //   Optional<NearByDriveResponse> matchedDriverOpt = nearbyDrivers.stream().findFirst();
        Optional<NearByDriveResponse> bestDriver = findBestDrive(nearbyDrivers);

        if(bestDriver.isEmpty()){
            log.info("No suitable driver found for ride request: {}", rideRequestedEvent.getRideId());
            return;
        }

        NearByDriveResponse assignDrive = bestDriver.get();
        RideMatchedEvent rideMatchedEvent = new RideMatchedEvent(
                rideRequestedEvent.getRideId(),
                rideRequestedEvent.getRiderId(),
                assignDrive.getDriverId(),
                assignDrive.getLatitude(),
                assignDrive.getLongitude(),
                assignDrive.getDistanceInKm()
        );

        kafkaTemplate.send(TOPIC, rideMatchedEvent.getRideId() ,rideMatchedEvent);
        log.info("Ride matched and event published: {}", rideMatchedEvent);
    }

    private Optional<NearByDriveResponse> findBestDrive(List<NearByDriveResponse> drivers) {

        double distanceWeight = 0.7; // Weight for distance (70%)
        double ratingWeight = 0.3;   // Weight for driver rating (30%)

        return drivers.stream()
                .max(Comparator.comparing(driver -> {
                    double distanceScore = 1.0/ (driver.getDistanceInKm() + 0.1); // Closer drivers get higher score
                    double simulateRating = 4.0 + Math.random(); // Simulate a driver rating between 4.0 and 5.0

                    return (distanceWeight * distanceScore) + (ratingWeight * simulateRating);
                }));
                 }

}
