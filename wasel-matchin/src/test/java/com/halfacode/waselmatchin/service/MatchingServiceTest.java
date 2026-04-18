package com.halfacode.waselmatchin.service;

import com.halfacode.waselmatchin.client.LocationServiceClient;
import com.halfacode.waselmatchin.dto.NearByDriveResponse;
import com.halfacode.waselmatchin.event.RideMatchedEvent;
import com.halfacode.waselmatchin.event.RideRequestedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private LocationServiceClient locationServiceClient;

    @Mock
    private KafkaTemplate<String, RideMatchedEvent> kafkaTemplate;

    @InjectMocks
    private MatchingService matchingService;

    @Test
    void matchDriverRide_doesNotPublishWhenNoNearbyDrivers() {
        RideRequestedEvent requestEvent = new RideRequestedEvent(
                "ride-1",
                "rider-1",
                24.7136,
                46.6753,
                "Pickup",
                24.7742,
                46.7385,
                "Drop"
        );

        when(locationServiceClient.getNearbyDrivers(anyDouble(), anyDouble(), anyDouble())).thenReturn(List.of());

        matchingService.matchDriverRide(requestEvent);

        verify(kafkaTemplate, never()).send(eq("ride.matched"), eq("ride-1"), org.mockito.ArgumentMatchers.any(RideMatchedEvent.class));
    }

    @Test
    void matchDriverRide_publishesMatchedEventWhenDriverExists() {
        RideRequestedEvent requestEvent = new RideRequestedEvent(
                "ride-2",
                "rider-2",
                24.7136,
                46.6753,
                "Pickup",
                24.7742,
                46.7385,
                "Drop"
        );

        NearByDriveResponse driver = new NearByDriveResponse("driver-1", 24.7140, 46.6760, 1.2);
        when(locationServiceClient.getNearbyDrivers(anyDouble(), anyDouble(), anyDouble())).thenReturn(List.of(driver));

        matchingService.matchDriverRide(requestEvent);

        ArgumentCaptor<RideMatchedEvent> eventCaptor = ArgumentCaptor.forClass(RideMatchedEvent.class);
        verify(kafkaTemplate).send(eq("ride.matched"), eq("ride-2"), eventCaptor.capture());

        RideMatchedEvent event = eventCaptor.getValue();
        assertEquals("ride-2", event.getRideId());
        assertEquals("rider-2", event.getRiderId());
        assertEquals("driver-1", event.getDriverId());
    }
}

