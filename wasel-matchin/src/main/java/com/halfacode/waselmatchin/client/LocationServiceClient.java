package com.halfacode.waselmatchin.client;

import com.halfacode.waselmatchin.dto.NearByDriveResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "wasel-location", url = "${location.service.url}")
public interface LocationServiceClient {

    @GetMapping("/api/v1/locations/drivers/nearby")
    List<NearByDriveResponse> getNearbyDrivers(
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam double radiusKm

    );
}

