package com.iag.smartpark.domain;

import java.math.BigDecimal;
import java.time.Duration;

public class PricingService {

    public BigDecimal calculate(Duration duration, boolean electric) {
        return new BigDecimal("2.50");
    }
}
