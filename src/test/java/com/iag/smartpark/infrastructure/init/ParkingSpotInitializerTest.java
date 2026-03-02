package com.iag.smartpark.infrastructure.init;


import com.iag.smartpark.domain.SlotStatus;
import com.iag.smartpark.infrastructure.repository.ParkingSpotRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ParkingSpotInitializerTest {

    @Autowired
    private ParkingSpotRepository spotRepository;

    @Test
    void shouldInitialize100Spots() {
        assertThat(spotRepository.count()).isEqualTo(100);
    }

    @Test
    void shouldHave20ElectricSpots() {
        assertThat(spotRepository.countByStatusAndHasCharger(SlotStatus.FREE, true))
                .isEqualTo(20);
    }

    @Test
    void shouldHave80NonElectricSpots() {
        assertThat(spotRepository.countByStatusAndHasCharger(SlotStatus.FREE, false))
                .isEqualTo(80);
    }
}