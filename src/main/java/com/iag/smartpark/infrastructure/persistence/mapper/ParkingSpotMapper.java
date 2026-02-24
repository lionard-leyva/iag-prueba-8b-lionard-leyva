package com.iag.smartpark.infrastructure.persistence.mapper;

import com.iag.smartpark.domain.ParkingSpot;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSpotEntity;
import org.springframework.stereotype.Component;

@Component
public class ParkingSpotMapper {

    public ParkingSpot toDomain(ParkingSpotEntity entity) {
        if (entity == null) {
            return null;
        }
        ParkingSpot spot = new ParkingSpot(entity.getId(), entity.isHasCharger());
        spot.setStatus(entity.getStatus());
        spot.setOccupiedBy(entity.getOccupiedBy());
        return spot;
    }

    public ParkingSpotEntity toEntity(ParkingSpot spot) {
        if (spot == null) {
            return null;
        }
        ParkingSpotEntity entity = new ParkingSpotEntity();
        entity.setId(spot.getId());
        entity.setHasCharger(spot.isHasCharger());
        entity.setStatus(spot.getStatus());
        entity.setOccupiedBy(spot.getOccupiedBy());
        return entity;
    }
}
