package com.iag.smartpark.domain.port;

import com.iag.smartpark.domain.ParkingSpot;
import com.iag.smartpark.domain.SlotStatus;

import java.util.Optional;

public interface ParkingSpotRepositoryPort {
    Optional<ParkingSpot> findFirstByStatusAndHasCharger(SlotStatus status, boolean hasCharger);
    Optional<ParkingSpot> findById(Integer id);
    Optional<ParkingSpot> findByOccupiedBy(String plate);
    ParkingSpot save(ParkingSpot spot);
}
