package com.iag.smartpark.infrastructure.persistence.repository;

import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Long> {

    Optional<ParkingSessionEntity> findByLicensePlateAndStatus(String plate, SessionStatus status);

    boolean existsByLicensePlateAndStatusIn(String plate, Collection<SessionStatus> statuses);

    long countByStatusIn(Collection<SessionStatus> statuses);

    long countByStatusInAndElectric(Collection<SessionStatus> statuses, boolean electric);
}
