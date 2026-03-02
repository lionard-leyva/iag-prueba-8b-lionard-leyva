package com.iag.smartpark.infrastructure.repository;

import com.iag.smartpark.domain.SlotStatus;
import com.iag.smartpark.infrastructure.entity.ParkingSpotEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Integer> {

    long countByStatusAndHasCharger(SlotStatus status, boolean hasCharger);

    long countByStatus(SlotStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ParkingSpotEntity> findFirstByStatusAndHasChargerOrderByIdAsc(SlotStatus status, boolean hasCharger);

    Optional<ParkingSpotEntity> findFirstByStatus(SlotStatus status);

    Optional<ParkingSpotEntity> findByOccupiedBy(String plate);
}
