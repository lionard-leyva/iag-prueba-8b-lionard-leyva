package com.iag.smartpark.infrastructure.persistence.repository;

import com.iag.smartpark.domain.SlotStatus;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Integer> {

    long countByStatusAndHasCharger(SlotStatus status, boolean hasCharger);

    long countByStatus(SlotStatus status);

    Optional<ParkingSpotEntity> findFirstByStatusAndHasCharger(SlotStatus status, boolean hasCharger);

    Optional<ParkingSpotEntity> findFirstByStatus(SlotStatus status);

    Optional<ParkingSpotEntity> findByOccupiedBy(String plate);
}
