package com.iag.smartpark.infrastructure.persistence.entity;

import com.iag.smartpark.domain.SessionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_sessions")
@Setter
@Getter
public class ParkingSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String licensePlate;
    private boolean electric;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @ManyToOne
    @JoinColumn(name = "spot_id")
    private ParkingSpotEntity assignedSpot;

    public ParkingSessionEntity() {}

    public ParkingSessionEntity(String licensePlate, boolean electric, LocalDateTime entryTime) {
        this.licensePlate = licensePlate;
        this.electric = electric;
        this.entryTime = entryTime;
        this.status = SessionStatus.ADMITTED;
    }
}
