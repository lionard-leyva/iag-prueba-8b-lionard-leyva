package com.iag.smartpark.infrastructure.init;

import com.iag.smartpark.infrastructure.entity.ParkingSpotEntity;
import com.iag.smartpark.infrastructure.repository.ParkingSpotRepository;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ParkingSpotInitializer {

    private static final int TOTAL_SPOTS = 100;
    private static final int ELECTRIC_SPOTS = 20;

    private final ParkingSpotRepository spotRepository;

    public ParkingSpotInitializer(ParkingSpotRepository spotRepository) {
        this.spotRepository = spotRepository;
    }

    @PostConstruct
    void init() {
        if (spotRepository.count() > 0) {
            return;
        }

        for (int i = 1; i <= TOTAL_SPOTS; i++) {
            boolean hasCharger = i <= ELECTRIC_SPOTS;
            ParkingSpotEntity spot = new ParkingSpotEntity();
            spot.setId(i);
            spot.setHasCharger(hasCharger);
            spotRepository.save(spot);
        }
    }
}
