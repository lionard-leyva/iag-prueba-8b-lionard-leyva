package com.iag.smartpark.domain.port;

import com.iag.smartpark.domain.ParkingSession;
import com.iag.smartpark.domain.SessionStatus;

import java.util.Optional;

public interface ParkingSessionRepositoryPort {
    Optional<ParkingSession> findByLicensePlateAndStatus(String plate, SessionStatus status);
    long countByStatus(SessionStatus status);
    long countByStatusAndElectric(SessionStatus status, boolean electric);
    ParkingSession save(ParkingSession session);
}
