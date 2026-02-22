package com.iag.smartpark.domain;

import java.math.BigDecimal;
import java.time.Duration;

public class PricingService {

    private static final BigDecimal RATE_STANDARD = new BigDecimal("2.50");
    private static final BigDecimal RATE_EXTRA = new BigDecimal("2.00");
    private static final BigDecimal ELECTRIC_SURCHARGE = new BigDecimal("3.50");
    private static final long STANDARD_HOURS_LIMIT = 3;

    public BigDecimal calculate(Duration duration, boolean electric) {
        long hours = (long) Math.ceil(duration.toMinutes() / 60.0);

        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException("Duration must be positive");
        }

        long standardHours = Math.min(hours, STANDARD_HOURS_LIMIT);
        long extraHours = Math.max(0, hours - STANDARD_HOURS_LIMIT);

        BigDecimal cost = RATE_STANDARD.multiply(BigDecimal.valueOf(standardHours))
                .add(RATE_EXTRA.multiply(BigDecimal.valueOf(extraHours)));

        if (electric) {
            cost = cost.add(ELECTRIC_SURCHARGE);
        }

        return cost;
    }
}
