package com.halfacode.waselride.service;

import com.halfacode.waselride.event.RideMatchedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {


    private final RideService rideService;


    @KafkaListener(
            topics = "ride.matched",
            groupId = "ride-service-group"
    )
    public void consumeRideEvent(RideMatchedEvent event) {
        log.info("Received ride event: {}", event);

        rideService.updateRideWithDriver(event.getRideId(),
                event.getDriverId());
    }
}
