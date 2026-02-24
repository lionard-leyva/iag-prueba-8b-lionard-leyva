package com.iag.smartpark.domain;


import lombok.Getter;
import lombok.Setter;

@Getter
public class ParkingSpot {
    private final Integer id;
    private final boolean hasCharger;
    @Setter
    private SlotStatus status;
    @Setter
    private String occupiedBy;

    public ParkingSpot(Integer id, boolean hasCharger) {
        this.id = id;
        this.hasCharger = hasCharger;
        this.status = SlotStatus.FREE;
    }

    public void occupy(String plate) {
        if (this.status == SlotStatus.OCCUPIED) {
            throw new IllegalStateException("Spot already occupied");
        }
        this.status = SlotStatus.OCCUPIED;
        this.occupiedBy = plate;
    }

    public void free() {
        this.status = SlotStatus.FREE;
        this.occupiedBy = null;
    }

    public boolean isFree() { return this.status == SlotStatus.FREE; }
}
