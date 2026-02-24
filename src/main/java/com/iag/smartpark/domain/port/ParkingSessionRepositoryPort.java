package com.iag.smartpark.domain.port;

import com.iag.smartpark.domain.ParkingSession;
import com.iag.smartpark.domain.SessionStatus;

import java.util.Collection;
import java.util.Optional;

public interface ParkingSessionRepositoryPort {
    Optional<ParkingSession> findByLicensePlateAndStatus(String plate, SessionStatus status);
    boolean existsByLicensePlateAndStatusIn(String plate, Collection<SessionStatus> statuses);
    long countByStatusIn(Collection<SessionStatus> statuses);
    long countByStatusInAndElectric(Collection<SessionStatus> statuses, boolean electric);
    ParkingSession save(ParkingSession session);
}
