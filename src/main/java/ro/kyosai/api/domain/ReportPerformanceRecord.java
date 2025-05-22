package ro.kyosai.api.domain;

import java.time.LocalDate;

public record ReportPerformanceRecord(String productId,
                                      LocalDate datetime,
                                      Integer availableTimestamp,
                                      Integer productionTimestamp,
                                      Integer lossTimestamp) {
    public ReportPerformanceRecord(String productId, LocalDate datetime, Integer availableTimestamp, Integer productionTimestamp, Integer lossTimestamp) {
        this.productId = productId;
        this.datetime = datetime;
        this.availableTimestamp = availableTimestamp;
        this.productionTimestamp = productionTimestamp;
        this.lossTimestamp = lossTimestamp;
    }
    public ReportPerformanceRecord(String productId, LocalDate datetime) {
        this(productId, datetime, null, null, null);
    }
    public ReportPerformanceRecord(Integer availableTimestamp, Integer productionTimestamp, Integer lossTimestamp) {
        this(null, null, availableTimestamp, productionTimestamp, lossTimestamp);
    }

}
