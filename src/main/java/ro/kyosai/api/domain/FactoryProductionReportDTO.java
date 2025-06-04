package ro.kyosai.api.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record FactoryProductionReportDTO(BigDecimal productionTimestamp,
        BigDecimal availableTimestamp,
        BigDecimal meanSpeed,
        BigDecimal idealSpeed,
        BigDecimal goodLinearMeters,
        BigDecimal totalLinearMeters,
        BigDecimal weight) {
    public static final BigDecimal getAsProcentage(final BigDecimal value0, final BigDecimal value1) {
        return value0.multiply(BigDecimal.valueOf(100))
                .divide(value1, 0, RoundingMode.HALF_UP);
    }

    public static final BigDecimal getOEEProcentage(final BigDecimal availability, final BigDecimal performance,
            final BigDecimal quality) {
        if (availability.intValue() == 0 || performance.intValue() == 0 || quality.intValue() == 0) {
            return BigDecimal.ZERO;
        }
        return availability.multiply(performance)
                .multiply(quality)
                .divide(BigDecimal.valueOf(100 * 100), 0, RoundingMode.HALF_UP);
    }
}
