package com.iag.smartpark.domain;

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

}
