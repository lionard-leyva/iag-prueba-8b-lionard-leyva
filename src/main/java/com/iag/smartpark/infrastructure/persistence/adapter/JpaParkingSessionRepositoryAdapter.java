package com.iag.smartpark.infrastructure.persistence.adapter;

import com.iag.smartpark.domain.ParkingSession;
import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.domain.port.ParkingSessionRepositoryPort;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSessionEntity;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSpotEntity;
import com.iag.smartpark.infrastructure.persistence.mapper.ParkingSessionMapper;
import com.iag.smartpark.infrastructure.persistence.repository.ParkingSessionRepository;
import com.iag.smartpark.infrastructure.persistence.repository.ParkingSpotRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaParkingSessionRepositoryAdapter implements ParkingSessionRepositoryPort {

    private final ParkingSessionRepository sessionRepository;
    private final ParkingSpotRepository spotRepository;
    private final ParkingSessionMapper sessionMapper;

    public JpaParkingSessionRepositoryAdapter(ParkingSessionRepository sessionRepository,
                                              ParkingSpotRepository spotRepository,
                                              ParkingSessionMapper sessionMapper) {
        this.sessionRepository = sessionRepository;
        this.spotRepository = spotRepository;
        this.sessionMapper = sessionMapper;
    }

    @Override
    public Optional<ParkingSession> findByLicensePlateAndStatus(String plate, SessionStatus status) {
        return sessionRepository.findByLicensePlateAndStatus(plate, status)
                .map(sessionMapper::toDomain);
    }

    @Override
    public long countByStatus(SessionStatus status) {
        return sessionRepository.countByStatus(status);
    }

    @Override
    public long countByStatusAndElectric(SessionStatus status, boolean electric) {
        return sessionRepository.countByStatusAndElectric(status, electric);
    }

    @Override
    public ParkingSession save(ParkingSession session) {
        ParkingSessionEntity entity = sessionMapper.toEntity(session);
        if (session.getAssignedSpot() != null) {
            ParkingSpotEntity spotRef = spotRepository.getReferenceById(session.getAssignedSpot().getId());
            entity.setAssignedSpot(spotRef);
        }
        ParkingSessionEntity saved = sessionRepository.save(entity);
        return sessionMapper.toDomain(saved);
    }
}
