package com.halfacode.waselmatchin.service;

import com.halfacode.waselmatchin.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {


    private final MatchingService matchingService;

    @KafkaListener(
           topics = "ride.requested",
           groupId = "wasel-matching-group"
    )
    public void consumeRideEvent(RideRequestedEvent rideRequestedEvent) {

        try {
            matchingService.matchDriverRide(rideRequestedEvent);
        } catch (Exception e) {
            log.error("Error processing ride event: {}", e.getMessage(), e);
        }
        log.info("Received ride event: {}", rideRequestedEvent);

    }
}
