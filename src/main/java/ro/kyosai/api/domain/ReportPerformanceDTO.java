package ro.kyosai.api.domain;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ReportPerformanceDTO {

    private String availableTime;
    private String productionTime;
    private String lossTime;
    private String productId;
    private LocalDate datetime;
    private Integer availableTimestamp;
    private Integer productionTimestamp;
    private Integer lossTimestamp;

    public ReportPerformanceDTO(String availableTime, String productionTime, String lossTime) {
        this.availableTime = availableTime;
        this.productionTime = productionTime;
        this.lossTime = lossTime;
    }
    public ReportPerformanceDTO(Integer availableTimestamp, Integer productionTimestamp, Integer lossTimestamp) {
        this.availableTimestamp = availableTimestamp;
        this.productionTimestamp = productionTimestamp;
        this.lossTimestamp = lossTimestamp;
    }

    public ReportPerformanceDTO(String productId, LocalDate datetime, Integer availableTimestamp, Integer productionTimestamp, Integer lossTimestamp) {
        this.productId = productId;
        this.datetime = datetime;
        this.availableTimestamp = availableTimestamp;
        this.productionTimestamp = productionTimestamp;
        this.lossTimestamp = lossTimestamp;
    }

    
}
