package com.iag.smartpark.infrastructure.entity;

import com.iag.smartpark.domain.SlotStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "parking_spots")
public class ParkingSpotEntity {

    @Id
    private Integer id;

    private boolean hasCharger;

    @Enumerated(EnumType.STRING)
    private SlotStatus status = SlotStatus.FREE;

    private String occupiedBy;

    public ParkingSpotEntity() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isHasCharger() {
        return hasCharger;
    }

    public void setHasCharger(boolean hasCharger) {
        this.hasCharger = hasCharger;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public String getOccupiedBy() {
        return occupiedBy;
    }

    public void setOccupiedBy(String occupiedBy) {
        this.occupiedBy = occupiedBy;
    }
}
