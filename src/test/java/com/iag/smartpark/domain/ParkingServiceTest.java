package com.iag.smartpark.domain;

import com.iag.smartpark.application.ParkingService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ParkingServiceTest {

    private final ParkingService parkingService = new ParkingService();

    @Test
    void shouldAllowEntryWhenCapacityAvailable() {
        boolean accepted = parkingService.registerEntry("ABC123", false);
        assertThat(accepted).isTrue();
    }

    @Test
    void shouldRejectEntryWhenParkingIsFull() {

        for (int i = 0; i < 100; i++) {
            parkingService.registerEntry("CAR" + i, false);
        }

        boolean accepted = parkingService.registerEntry("OVERFLOW", false);
        assertThat(accepted).isFalse();
    }

    @Test
    void shouldRejectElectricWhenNoElectricSpotsAvailable() {

        for (int i = 0; i < 20; i++) {
            parkingService.registerEntry("ELEC" + i, true);
        }

        boolean accepted = parkingService.registerEntry("ELEC_OVER", true);
        assertThat(accepted).isFalse();
    }

    @Test
    void shouldCreateSessionOnEntry() {

        parkingService.registerEntry("ABC123", false);
        assertThat(parkingService.hasActiveSession("ABC123")).isTrue();
    }

    @Test
    void shouldRejectIfVehicleAlreadyInside() {

        parkingService.registerEntry("ABC123", false);
        boolean accepted = parkingService.registerEntry("ABC123", false);
        assertThat(accepted).isFalse();
    }

    @Test
    void shouldNotIncreaseOccupiedSpotsWhenDuplicateRejected() {

        parkingService.registerEntry("ABC123", false);
        parkingService.registerEntry("ABC123", false);
        assertThat(parkingService.getOccupiedSpots()).isEqualTo(1);
    }

    @Test
    void shouldIncreaseElectricCounterOnlyForElectricVehicles() {

        parkingService.registerEntry("ELEC1", true);
        assertThat(parkingService.getOccupiedElectricSpots()).isEqualTo(1);
    }

    @Test
    void shouldCloseSessionAndReturnItOnExit() {

        parkingService.registerEntry("ABC123", false);

        ParkingSession session = parkingService.registerExit("ABC123");

        assertThat(session).isNotNull();
        assertThat(session.isActive()).isFalse();
        assertThat(parkingService.hasActiveSession("ABC123")).isFalse();
    }

    @Test
    void shouldCalculateAndStoreTotalCostOnExit() {

        parkingService.registerEntry("ABC123", false);

        ParkingSession session = parkingService.getActiveSession("ABC123");
        session = new ParkingSession(
                session.getPlate(),
                session.isElectric(),
                session.getEntryTime().minusHours(1)
        );

        parkingService.replaceSessionForTest("ABC123", session);

        ParkingSession closed = parkingService.registerExit("ABC123");

        assertThat(closed.getTotalCost()).isEqualByComparingTo("2.50");
        assertThat(parkingService.hasActiveSession("ABC123")).isFalse();
        assertThat(parkingService.getOccupiedSpots()).isEqualTo(0);
    }

    @Test
    void shouldAssignFreeSpotOnOccupation() {

        ParkingService service = new ParkingService();

        service.registerEntry("ABC123", false);

        service.assignSpot("ABC123");

        ParkingSpot spot = service.findSpotByPlate("ABC123");

        assertThat(spot).isNotNull();
        assertThat(spot.getOccupiedBy()).isEqualTo("ABC123");
    }

    @Test
    void shouldAllowChangingSpot() {

        ParkingService service = new ParkingService();

        service.registerEntry("ABC123", false);
        service.assignSpot("ABC123");

        ParkingSpot first = service.findSpotByPlate("ABC123");

        service.changeSpot("ABC123");

        ParkingSpot second = service.findSpotByPlate("ABC123");

        assertThat(second.getId()).isNotEqualTo(first.getId());
    }

}
