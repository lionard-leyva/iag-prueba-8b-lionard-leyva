package com.iag.smartpark.domain;

public class ParkingService {

    private static final int TOTAL_SPOTS = 100;
    private static final int ELECTRIC_SPOTS = 20;

    private int occupiedSpots = 0;
    private int occupiedElectricSpots = 0;

    public boolean registerEntry(String plate, boolean electric) {

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

        return true;
    }
}
