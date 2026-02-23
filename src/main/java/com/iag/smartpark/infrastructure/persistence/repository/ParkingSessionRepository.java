package com.iag.smartpark.infrastructure.persistence.repository;

import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Long> {

    Optional<ParkingSessionEntity> findByLicensePlateAndStatus(String plate, SessionStatus status);

    long countByStatus(SessionStatus status);

    long countByStatusAndElectric(SessionStatus status, boolean electric);
}
