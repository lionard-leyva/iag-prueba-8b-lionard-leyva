package com.iag.smartpark.domain;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
public class ParkingSession {

    @Setter
    private Long id;
    private final String licensePlate;
    private final boolean electric;
    private final LocalDateTime entryTime;
    @Setter
    private LocalDateTime exitTime;
    @Setter
    private BigDecimal totalCost;
    @Setter
    private SessionStatus status;
    @Setter
    private ParkingSpot assignedSpot;


    public ParkingSession(String licensePlate, boolean electric,
                          LocalDateTime entryTime, LocalDateTime exitTime,
                          BigDecimal totalCost, SessionStatus status) {
        this.licensePlate = licensePlate;
        this.electric = electric;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.totalCost = totalCost;
        this.status = status;
    }

    public ParkingSession(String licensePlate, boolean electric, LocalDateTime entryTime) {
        this.licensePlate = licensePlate;
        this.electric = electric;
        this.entryTime = entryTime;
        this.status = SessionStatus.ADMITTED;
    }


    public void park(ParkingSpot spot) {
        if (this.status != SessionStatus.ADMITTED) {
            throw new IllegalStateException("Session must be ADMITTED to park");
        }
        this.assignedSpot = spot;
        this.status = SessionStatus.PARKED;
    }

    public void close(LocalDateTime exitTime) {
        if (this.status == SessionStatus.EXITED) {
            throw new IllegalStateException("Session already closed");
        }
        this.exitTime = exitTime;
        this.status = SessionStatus.EXITED;
    }

    public void changeSpot(ParkingSpot spot) {
        if (this.status != SessionStatus.PARKED) {
            throw new IllegalStateException("Session must be PARKED to change spot");
        }
        this.assignedSpot = spot;
    }

    public boolean isActive() { return this.status != SessionStatus.EXITED; }
}
