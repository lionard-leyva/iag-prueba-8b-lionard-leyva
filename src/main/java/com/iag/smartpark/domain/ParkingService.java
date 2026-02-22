package com.iag.smartpark.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ParkingService {

    private final PricingService pricingService = new PricingService();
    private final List<ParkingSpot> spots = new ArrayList<>();
    private final Map<String, ParkingSession> activeSessions = new HashMap<>();

    private static final int TOTAL_SPOTS = 100;
    private static final int ELECTRIC_SPOTS = 20;

    private int occupiedSpots = 0;
    private int occupiedElectricSpots = 0;

    public ParkingService() {
        for (int i = 1; i <= 100; i++) {
            boolean electric = i <= 20;
            spots.add(new ParkingSpot(i, electric));
        }
    }

    public boolean registerEntry(String plate, boolean electric) {

        if (activeSessions.containsKey(plate)) {
            return false;
        }

        if (occupiedSpots >= TOTAL_SPOTS) {
            return false;
        }

        if (electric && occupiedElectricSpots >= ELECTRIC_SPOTS) {
            return false;
        }

        occupiedSpots++;

        if (electric) {
            occupiedElectricSpots++;
        }

        activeSessions.put(
                plate,
                new ParkingSession(plate, electric, LocalDateTime.now())
        );

        return true;
    }

    public ParkingSession registerExit(String plate) {

        ParkingSession session = activeSessions.get(plate);

        if (session == null) {
            throw new IllegalArgumentException("Vehicle not inside parking");
        }

        LocalDateTime exitTime = LocalDateTime.now();
        session.close(exitTime);

        Duration duration = Duration.between(session.getEntryTime(), exitTime);

        BigDecimal cost = pricingService.calculate(duration, session.isElectric());
        session.setTotalCost(cost);

        activeSessions.remove(plate);

        occupiedSpots--;

        if (session.isElectric()) {
            occupiedElectricSpots--;
        }

        return session;
    }

    public ParkingSpot findSpotByPlate(String plate) {
        return spots.stream()
                .filter(s -> plate.equals(s.getOccupiedBy()))
                .findFirst()
                .orElse(null);
    }
    public void changeSpot(String plate) {

        ParkingSpot current = findSpotByPlate(plate);

        if (current == null) {
            assignSpot(plate);
            return;
        }

        current.free();

        ParkingSession session = activeSessions.get(plate);
        if (session == null) {
            throw new IllegalArgumentException("Vehicle not inside");
        }

        for (ParkingSpot spot : spots) {
            if (spot.isFree() &&
                    spot.getId() != current.getId() &&
                    (!session.isElectric() || spot.isElectric())) {

                spot.occupy(plate);
                return;
            }
        }
        current.occupy(plate);
    }

    public void assignSpot(String plate) {

        ParkingSession session = activeSessions.get(plate);

        if (session == null) {
            throw new IllegalArgumentException("Vehicle not inside");
        }

        for (ParkingSpot spot : spots) {
            if (spot.isFree() &&
                    (!session.isElectric() || spot.isElectric())) {

                spot.occupy(plate);
                return;
            }
        }

        throw new IllegalStateException("No suitable spot available");
    }

    public boolean hasActiveSession(String plate) {
        return activeSessions.containsKey(plate);
    }

    ParkingSession getActiveSession(String plate) {
        return activeSessions.get(plate);
    }

    void replaceSessionForTest(String plate, ParkingSession session) {  activeSessions.put(plate, session);}

    int getOccupiedSpots() { return occupiedSpots; }

    int getOccupiedElectricSpots() { return occupiedElectricSpots; }

}
