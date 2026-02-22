package com.iag.smartpark.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class ParkingSession {

    private final String plate;
    private final boolean electric;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    @Setter
    private BigDecimal totalCost;

    public ParkingSession(String plate, boolean electric, LocalDateTime entryTime) {
        this.plate = plate;
        this.electric = electric;
        this.entryTime = entryTime;
    }

    public boolean isActive() {
        return exitTime == null;
    }

    public void close(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}