package com.iag.smartpark.application;


import java.time.Duration;
import java.time.LocalDateTime;


import com.iag.smartpark.domain.ParkingSession;
import com.iag.smartpark.domain.ParkingSpot;
import com.iag.smartpark.domain.ParkingRateCalculator;
import com.iag.smartpark.domain.SessionStatus;
import com.iag.smartpark.domain.SlotStatus;
import com.iag.smartpark.domain.port.ParkingSessionRepositoryPort;
import com.iag.smartpark.domain.port.ParkingSpotRepositoryPort;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class ParkingUseCaseService {

    private static final int TOTAL_SPOTS = 100;
    private static final int ELECTRIC_SPOTS = 20;

    private final ParkingSpotRepositoryPort spotRepository;
    private final ParkingSessionRepositoryPort sessionRepository;
    private final ParkingRateCalculator parkingRateCalculator = new ParkingRateCalculator();

    public ParkingUseCaseService(ParkingSpotRepositoryPort spotRepository,
                                 ParkingSessionRepositoryPort sessionRepository) {
        this.spotRepository = spotRepository;
        this.sessionRepository = sessionRepository;
    }

    public boolean registerEntry(String plate, boolean electric) {
        if (sessionRepository.findByLicensePlateAndStatus(plate, SessionStatus.ADMITTED).isPresent())
            return false;
        if (sessionRepository.countByStatus(SessionStatus.ADMITTED) >= TOTAL_SPOTS)
            return false;
        if (electric && sessionRepository.countByStatusAndElectric(SessionStatus.ADMITTED, true) >= ELECTRIC_SPOTS)
            return false;

        sessionRepository.save(new ParkingSession(plate, electric, LocalDateTime.now()));
        return true;
    }

    public ParkingSession registerExit(String plate) {
        ParkingSession session = sessionRepository
                .findByLicensePlateAndStatus(plate, SessionStatus.ADMITTED)
                .or(() -> sessionRepository.findByLicensePlateAndStatus(plate, SessionStatus.PARKED))
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not inside parking"));

        session.close(LocalDateTime.now());

        boolean usedCharger = session.getAssignedSpot() != null && session.getAssignedSpot().isHasCharger();
        session.setTotalCost(parkingRateCalculator.calculate(
                Duration.between(session.getEntryTime(), session.getExitTime()), usedCharger));

        if (session.getAssignedSpot() != null) {
            ParkingSpot spot = spotRepository.findById(session.getAssignedSpot().getId()).orElseThrow();
            spot.free();
            spotRepository.save(spot);
        }

        sessionRepository.save(session);
        return session;
    }

    public void assignSpot(String plate) {
        ParkingSession session = sessionRepository
                .findByLicensePlateAndStatus(plate, SessionStatus.ADMITTED)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not inside"));

        ParkingSpot spot = findAvailableSpot(session.isElectric());

        session.park(spot);
        spot.occupy(plate);
        spotRepository.save(spot);
        sessionRepository.save(session);
    }

    public void changeSpot(String plate) {
        ParkingSession session = sessionRepository
                .findByLicensePlateAndStatus(plate, SessionStatus.PARKED)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not inside"));

        ParkingSpot current = spotRepository.findById(session.getAssignedSpot().getId()).orElseThrow();
        current.free();
        spotRepository.save(current);

        ParkingSpot next = findAvailableSpot(session.isElectric());
        session.changeSpot(next);
        next.occupy(plate);
        spotRepository.save(next);
        sessionRepository.save(session);
    }

    private ParkingSpot findAvailableSpot(boolean electric) {
        return electric
                ? spotRepository.findFirstByStatusAndHasCharger(SlotStatus.FREE, true)
                .orElseThrow(() -> new IllegalStateException("No electric spot available"))
                : spotRepository.findFirstByStatusAndHasCharger(SlotStatus.FREE, false)
                .orElseThrow(() -> new IllegalStateException("No spot available"));
    }
}
