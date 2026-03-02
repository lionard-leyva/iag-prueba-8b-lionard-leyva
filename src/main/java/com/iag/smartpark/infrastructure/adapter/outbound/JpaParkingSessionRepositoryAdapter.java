package com.iag.smartpark.infrastructure.adapter.outbound;

import com.iag.smartpark.domain.ParkingSession;
import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.domain.port.ParkingSessionRepositoryPort;
import com.iag.smartpark.infrastructure.entity.ParkingSessionEntity;
import com.iag.smartpark.infrastructure.entity.ParkingSpotEntity;
import com.iag.smartpark.infrastructure.mapper.ParkingSessionMapper;
import com.iag.smartpark.infrastructure.repository.ParkingSessionRepository;
import com.iag.smartpark.infrastructure.repository.ParkingSpotRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
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
    public boolean hasActiveSessionForUpdate(String plate, Collection<SessionStatus> statuses) {
        return sessionRepository.findFirstByLicensePlateAndStatusIn(plate, statuses).isPresent();
    }

    @Override
    public boolean existsByLicensePlateAndStatusIn(String plate, Collection<SessionStatus> statuses) {
        return sessionRepository.existsByLicensePlateAndStatusIn(plate, statuses);
    }

    @Override
    public long countByStatusIn(Collection<SessionStatus> statuses) {
        return sessionRepository.countByStatusIn(statuses);
    }

    @Override
    public long countByStatusInAndElectric(Collection<SessionStatus> statuses, boolean electric) {
        return sessionRepository.countByStatusInAndElectric(statuses, electric);
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
