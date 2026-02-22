package com.iag.smartpark.domain;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    void shouldCharge250ForOneHour() {
        BigDecimal cost = pricingService.calculate(Duration.ofHours(1),false);
        assertThat(cost).isEqualByComparingTo("2.50");
    }
}