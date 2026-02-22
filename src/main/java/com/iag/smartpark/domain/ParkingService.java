package com.iag.smartpark.domain;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ParkingService {

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

    int getOccupiedSpots() { return occupiedSpots; }

    int getOccupiedElectricSpots() { return occupiedElectricSpots; }

    public boolean hasActiveSession(String plate) {
        return activeSessions.containsKey(plate);
    }
}
