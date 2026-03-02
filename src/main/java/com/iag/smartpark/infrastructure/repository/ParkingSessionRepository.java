package com.iag.smartpark.infrastructure.repository;

import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.infrastructure.entity.ParkingSessionEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Long> {

    Optional<ParkingSessionEntity> findByLicensePlateAndStatus(String plate, SessionStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ParkingSessionEntity> findFirstByLicensePlateAndStatusIn(String plate, Collection<SessionStatus> statuses);

    boolean existsByLicensePlateAndStatusIn(String plate, Collection<SessionStatus> statuses);

    long countByStatusIn(Collection<SessionStatus> statuses);

    long countByStatusInAndElectric(Collection<SessionStatus> statuses, boolean electric);
}
