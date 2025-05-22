package ro.kyosai.api.domain;

import java.math.BigDecimal;

public record FactoryReportTotalsDTO(BigDecimal oeeSum,
                                     BigDecimal qualitySum,
                                     BigDecimal availabilitySum,
                                     BigDecimal performanceSum,
                                     BigDecimal weightSum) {
    public FactoryReportTotalsDTO(BigDecimal oeeSum, BigDecimal qualitySum, BigDecimal availabilitySum, BigDecimal performanceSum, BigDecimal weightSum) {
        this.oeeSum = oeeSum;
        this.qualitySum = qualitySum;
        this.availabilitySum = availabilitySum;
        this.performanceSum = performanceSum;
        this.weightSum = weightSum;
    }

}
