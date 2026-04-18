package com.halfacode.waselride.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(nullable = false)
    private String riderId;
    @Column(nullable = false)
    private double pickupLatitude;
    @Column(nullable = false)
    private double pickupLongitude;
    private String pickupAddress;
    @Column(nullable = false)
    private double dropLatitude;
    @Column(nullable = false)
    private double dropLongitude;
    @Column(nullable = false)
    private String dropAddress;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RidsStatus status; // e.g., "REQUESTED", "ACCEPTED", "IN_PROGRESS", "COMPLETED", "CANCELLED"

    private double estimatedFare;
    private double actualFare;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;


}
