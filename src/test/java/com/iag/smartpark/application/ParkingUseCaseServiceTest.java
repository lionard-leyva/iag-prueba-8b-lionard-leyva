package com.iag.smartpark.application;

import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.domain.SlotStatus;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSessionEntity;
import com.iag.smartpark.infrastructure.persistence.entity.ParkingSpotEntity;
import com.iag.smartpark.infrastructure.persistence.repository.ParkingSessionRepository;
import com.iag.smartpark.infrastructure.persistence.repository.ParkingSpotRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ParkingUseCaseServiceTest {

    @Autowired
    private ParkingUseCaseService service;

    @Autowired
    private ParkingSessionRepository sessionRepository;

    @Autowired
    private ParkingSpotRepository spotRepository;

    @Test
    void shouldAllowEntryWhenCapacityAvailable() {
        assertThat(service.registerEntry("ABC123", false)).isTrue();
    }

    @Test
    void shouldRejectIfVehicleAlreadyInside() {
        service.registerEntry("ABC123", false);
        assertThat(service.registerEntry("ABC123", false)).isFalse();
    }

    @Test
    void shouldCreateSessionOnEntry() {
        service.registerEntry("ABC123", false);
        assertThat(sessionRepository.findByLicensePlateAndStatus("ABC123", SessionStatus.ADMITTED))
                .isPresent();
    }

    @Test
    void shouldCloseSessionOnExit() {
        service.registerEntry("ABC123", false);
        ParkingSessionEntity session = sessionRepository.findByLicensePlateAndStatus("ABC123", SessionStatus.ADMITTED)
                .orElseThrow();
        service.registerExit("ABC123");
        session = sessionRepository.findByLicensePlateAndStatus("ABC123", SessionStatus.EXITED).orElseThrow();
        assertThat(session.getStatus()).isEqualTo(SessionStatus.EXITED);
    }

    @Test
    void shouldCalculateCostOnExit() {
        service.registerEntry("ABC123", false);
        service.registerExit("ABC123");
        ParkingSessionEntity session = sessionRepository.findByLicensePlateAndStatus("ABC123", SessionStatus.EXITED).orElseThrow();
        assertThat(session.getTotalCost()).isNotNull();
    }

    @Test
    void shouldAssignSpotOnOccupation() {
        service.registerEntry("ABC123", false);
        service.assignSpot("ABC123");
        Optional<ParkingSpotEntity> spot = spotRepository.findByOccupiedBy("ABC123");
        assertThat(spot).isPresent();
    }

    @Test
    void shouldFreeSpotOnExit() {
        service.registerEntry("ABC123", false);
        service.assignSpot("ABC123");
        service.registerExit("ABC123");
        assertThat(spotRepository.findByOccupiedBy("ABC123")).isEmpty();
    }

    @Test
    void shouldChangeSpotWhenVehicleIsParked() {
        service.registerEntry("ABC123", false);
        service.assignSpot("ABC123");
        Integer firstSpotId = spotRepository.findByOccupiedBy("ABC123")
                .map(ParkingSpotEntity::getId)
                .orElseThrow();

        service.changeSpot("ABC123");

        ParkingSpotEntity currentSpot = spotRepository.findByOccupiedBy("ABC123").orElseThrow();
        assertThat(currentSpot.getId()).isNotEqualTo(firstSpotId);
        assertThat(currentSpot.getStatus()).isEqualTo(SlotStatus.OCCUPIED);
        assertThat(currentSpot.getOccupiedBy()).isEqualTo("ABC123");
        assertThat(sessionRepository.findByLicensePlateAndStatus("ABC123", SessionStatus.PARKED)).isPresent();
    }
}
