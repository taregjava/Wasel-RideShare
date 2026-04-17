package com.halfacode.waselride.repository;

import com.halfacode.waselride.model.Ride;
import com.halfacode.waselride.model.RidsStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends JpaRepository<Ride, String> {

    List<Ride> findByRiderId(String riderId);

    List<Ride> findByStatus(RidsStatus status);

    List<Ride> findByRiderIdOrderByCreatedAtDesc(String riderId);

    Ride findByRiderIdAndStatus(String riderId, RidsStatus ridsStatus);
}

