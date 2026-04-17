package com.halfacode.wasellocation.service;

import com.halfacode.wasellocation.dto.DriveLocationRequest;
import com.halfacode.wasellocation.dto.NearByDriveResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    public static final String DRIVER_GEO_KEY="drivers:locations";

    public final RedisTemplate<String, String> redisTemplate;
    public void updateDriverLocation(DriveLocationRequest driveLocationRequest) {
        log.info("Updating location for driverId: {}," ,driveLocationRequest.getDriveId());

        Point point= new Point(
                driveLocationRequest.getLongitude(),
                driveLocationRequest.getLatitude());

        redisTemplate.opsForGeo().add(
                DRIVER_GEO_KEY, point,
                driveLocationRequest.getDriveId());
    }

    public  List<NearByDriveResponse> getNearbyDrivers(double latitude, double longitude, double radiusKm) {

        log.info("Finding nearby drivers for location: ({}, {}) with radius: {} km", latitude, longitude, radiusKm);


        Circle searchArea = new Circle(new Point(longitude, latitude),
             new Distance(radiusKm, Metrics.KILOMETERS)   ); // Convert km to radians radiusKm / 6371.0

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo()
                .radius(DRIVER_GEO_KEY,
                        searchArea
                , RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates()
                                .includeCoordinates()
                                .includeDistance()
                                .sortAscending()
                                .limit(10) // Limit to top 10 results
                );

        List<NearByDriveResponse> nearbyDrivers = new ArrayList<>();
        if (results != null) {
            results.getContent().forEach(result -> {
                RedisGeoCommands.GeoLocation<String> location = result.getContent();
                nearbyDrivers.add(new NearByDriveResponse(
                        location.getName(),
                        location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getDistance().getValue()
                ));
            });
        }
        log.info("Found {} nearby drivers", nearbyDrivers.size());
        return nearbyDrivers;

    }

    public void deleteDriverLocation(String driverId) {
        log.info("Deleting location for driverId: {}," ,driverId);
        redisTemplate.opsForGeo().remove(DRIVER_GEO_KEY, driverId);
    }
}
