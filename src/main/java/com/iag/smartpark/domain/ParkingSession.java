package com.iag.smartpark.domain;

import java.time.LocalDateTime;

public class ParkingSession {

    private final String plate;
    private final boolean electric;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public ParkingSession(String plate, boolean electric, LocalDateTime entryTime) {
        this.plate = plate;
        this.electric = electric;
        this.entryTime = entryTime;
    }

    public String getPlate() {
        return plate;
    }

    public boolean isElectric() {
        return electric;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public boolean isActive() {
        return exitTime == null;
    }

    public void close(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}