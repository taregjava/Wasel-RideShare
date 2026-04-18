package com.halfacode.waselride.service;

import com.halfacode.waselride.dto.RideRequest;
import com.halfacode.waselride.dto.RideResponse;
import com.halfacode.waselride.event.RideRequestEvent;
import com.halfacode.waselride.model.Ride;
import com.halfacode.waselride.model.RidsStatus;
import com.halfacode.waselride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private KafkaTemplate<String, RideRequestEvent> kafkaTemplate;

    private RideService rideService;

    @BeforeEach
    void setUp() {
        rideService = new RideService(rideRepository, kafkaTemplate);
        ReflectionTestUtils.setField(rideService, "rideRequestedTopic", "ride.requested");
    }

    @Test
    void createRide_publishesRequestedEvent_andReturnsMatchingStatus() {
        RideRequest request = new RideRequest();
        request.setRiderId("rider-1");
        request.setPickupLatitude(24.7136);
        request.setPickupLongitude(46.6753);
        request.setPickupAddress("Pickup");
        request.setDropLatitude(24.7742);
        request.setDropLongitude(46.7385);
        request.setDropAddress("Drop");

        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            if (ride.getId() == null) {
                ride.setId("ride-1");
            }
            return ride;
        });

        RideResponse response = rideService.createRide(request);

        assertEquals("ride-1", response.getId());
        assertEquals(RidsStatus.MATCHING, response.getStatus());
        assertTrue(response.getEstimatedFare() > 0);

        verify(rideRepository, times(2)).save(any(Ride.class));

        ArgumentCaptor<RideRequestEvent> eventCaptor = ArgumentCaptor.forClass(RideRequestEvent.class);
        verify(kafkaTemplate).send(org.mockito.ArgumentMatchers.eq("ride.requested"), org.mockito.ArgumentMatchers.eq("ride-1"), eventCaptor.capture());

        RideRequestEvent event = eventCaptor.getValue();
        assertEquals("ride-1", event.getRideId());
        assertEquals("rider-1", event.getRiderId());
        assertEquals(RidsStatus.REQUESTED, event.getStatus());
    }

    @Test
    void startRide_throwsWhenStatusIsNotAccepted() {
        Ride ride = new Ride();
        ride.setId("ride-2");
        ride.setStatus(RidsStatus.REQUESTED);

        when(rideRepository.findById("ride-2")).thenReturn(Optional.of(ride));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> rideService.startRide("ride-2"));

        assertTrue(ex.getMessage().contains("Ride cannot be started"));
        verify(rideRepository, never()).save(any(Ride.class));
    }
}

