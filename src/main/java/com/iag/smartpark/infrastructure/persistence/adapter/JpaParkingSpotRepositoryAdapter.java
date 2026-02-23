package com.iag.smartpark.infrastructure.persistence.adapter;

import com.iag.smartpark.domain.ParkingSpot;
import com.iag.smartpark.domain.SlotStatus;
import com.iag.smartpark.domain.port.ParkingSpotRepositoryPort;
import com.iag.smartpark.infrastructure.persistence.mapper.ParkingSpotMapper;
import com.iag.smartpark.infrastructure.persistence.repository.ParkingSpotRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaParkingSpotRepositoryAdapter implements ParkingSpotRepositoryPort {

    private final ParkingSpotRepository spotRepository;
    private final ParkingSpotMapper spotMapper;

    public JpaParkingSpotRepositoryAdapter(ParkingSpotRepository spotRepository,
                                           ParkingSpotMapper spotMapper) {
        this.spotRepository = spotRepository;
        this.spotMapper = spotMapper;
    }

    @Override
    public Optional<ParkingSpot> findFirstByStatusAndHasCharger(SlotStatus status, boolean hasCharger) {
        return spotRepository.findFirstByStatusAndHasCharger(status, hasCharger)
                .map(spotMapper::toDomain);
    }

    @Override
    public Optional<ParkingSpot> findById(Integer id) {
        return spotRepository.findById(id).map(spotMapper::toDomain);
    }

    @Override
    public Optional<ParkingSpot> findByOccupiedBy(String plate) {
        return spotRepository.findByOccupiedBy(plate).map(spotMapper::toDomain);
    }

    @Override
    public ParkingSpot save(ParkingSpot spot) {
        return spotMapper.toDomain(spotRepository.save(spotMapper.toEntity(spot)));
    }
}
