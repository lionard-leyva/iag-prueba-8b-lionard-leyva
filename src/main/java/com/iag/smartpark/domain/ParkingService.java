package com.iag.smartpark.domain;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ParkingService {

    private final PricingService pricingService = new PricingService();

    private final Map<String, ParkingSession> activeSessions = new HashMap<>();

    private static final int TOTAL_SPOTS = 100;
    private static final int ELECTRIC_SPOTS = 20;

    private int occupiedSpots = 0;
    private int occupiedElectricSpots = 0;

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

    public boolean hasActiveSession(String plate) {
        return activeSessions.containsKey(plate);
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

    ParkingSession getActiveSession(String plate) {
        return activeSessions.get(plate);
    }

    void replaceSessionForTest(String plate, ParkingSession session) {
        activeSessions.put(plate, session);
    }

    int getOccupiedSpots() { return occupiedSpots; }

    int getOccupiedElectricSpots() { return occupiedElectricSpots; }
}
