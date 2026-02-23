package com.iag.smartpark.domain;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class ParkingRateCalculatorTest {

    private final ParkingRateCalculator parkingRateCalculator = new ParkingRateCalculator();

    @Test
    void shouldCharge250ForOneHour() {
        BigDecimal cost = parkingRateCalculator.calculate(Duration.ofHours(1),false);
        assertThat(cost).isEqualByComparingTo("2.50");
    }

    @Test
    void shouldCharge750ForThreeHours() {
        BigDecimal cost = parkingRateCalculator.calculate(Duration.ofHours(3), false);
        assertThat(cost).isEqualByComparingTo("7.50");
    }

    @Test
    void shouldCharge950ForFourHours() {
        BigDecimal cost = parkingRateCalculator.calculate(Duration.ofHours(4), false);
        assertThat(cost).isEqualByComparingTo("9.50");
    }

    @Test
    void shouldChargeForFractionAsFullHour() {
        BigDecimal cost = parkingRateCalculator.calculate(Duration.ofHours(3).plusMinutes(1), false);
        assertThat(cost).isEqualByComparingTo("9.50");
    }

    @Test
    void shouldChargeMinimumOneHourForPositiveDuration() {
        BigDecimal cost = parkingRateCalculator.calculate(Duration.ofMinutes(1), false);
        assertThat(cost).isEqualByComparingTo("2.50");
    }

    @Test
    void shouldAddElectricSurcharge() {
        BigDecimal cost = parkingRateCalculator.calculate(Duration.ofHours(1), true);
        assertThat(cost).isEqualByComparingTo("6.00");
    }
}
