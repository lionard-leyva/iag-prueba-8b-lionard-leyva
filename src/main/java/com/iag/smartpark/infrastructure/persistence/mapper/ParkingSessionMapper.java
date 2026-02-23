package com.iag.smartpark.infrastructure.persistence.mapper;

import com.iag.smartpark.domain.ParkingSession;
import com.iag.smartpark.domain.ParkingSpot;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSessionEntity;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSpotEntity;
import org.springframework.stereotype.Component;

@Component
public class ParkingSessionMapper {

    private final ParkingSpotMapper spotMapper;

    public ParkingSessionMapper(ParkingSpotMapper spotMapper) {
        this.spotMapper = spotMapper;
    }

    public ParkingSession toDomain(ParkingSessionEntity entity) {
        if (entity == null) {
            return null;
        }
        ParkingSession session = new ParkingSession(
                entity.getLicensePlate(),
                entity.isElectric(),
                entity.getEntryTime(),
                entity.getExitTime(),
                entity.getTotalCost(),
                entity.getStatus()
        );
        session.setId(entity.getId());
        ParkingSpotEntity assigned = entity.getAssignedSpot();
        if (assigned != null) {
            ParkingSpot spot = spotMapper.toDomain(assigned);
            session.setAssignedSpot(spot);
        }
        return session;
    }

    public ParkingSessionEntity toEntity(ParkingSession session) {
        if (session == null) {
            return null;
        }
        ParkingSessionEntity entity = new ParkingSessionEntity();
        entity.setId(session.getId());
        entity.setLicensePlate(session.getLicensePlate());
        entity.setElectric(session.isElectric());
        entity.setEntryTime(session.getEntryTime());
        entity.setExitTime(session.getExitTime());
        entity.setTotalCost(session.getTotalCost());
        entity.setStatus(session.getStatus());
        return entity;
    }
}
