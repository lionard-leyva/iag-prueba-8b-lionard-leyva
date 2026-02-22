package com.iag.smartpark.domain;

public class ParkingSpot {

    private final int id;
    private final boolean electric;
    private String occupiedBy; // plate

    public ParkingSpot(int id, boolean electric) {
        this.id = id;
        this.electric = electric;
    }

    public boolean isFree() {
        return occupiedBy == null;
    }

    public void occupy(String plate) {
        this.occupiedBy = plate;
    }

    public void free() {
        this.occupiedBy = null;
    }

    public boolean isElectric() {
        return electric;
    }

    public String getOccupiedBy() {
        return occupiedBy;
    }

    public int getId() {
        return id;
    }
}
