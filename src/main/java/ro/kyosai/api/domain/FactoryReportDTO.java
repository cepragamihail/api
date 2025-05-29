package ro.kyosai.api.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class FactoryReportDTO {

    private String productId;
    private BigDecimal weight;
    private LocalDate datetime;
    private BigDecimal oee;
    private BigDecimal availability;
    private BigDecimal performance;
    private BigDecimal quality;
    private String productionTimestamp;
    private String lossTimestamp;
    private String availableTimestamp;

    public FactoryReportDTO(String productId, BigDecimal weight, LocalDate datetime, BigDecimal oee,
                            BigDecimal availability, BigDecimal performance, BigDecimal quality,
                            String productionTimestamp, String lossTimestamp, String availableTimestamp) {
        this.productId = productId;
        this.weight = weight;
        this.datetime = datetime;
        this.oee = oee;
        this.availability = availability;
        this.performance = performance;
        this.quality = quality;
        this.productionTimestamp = productionTimestamp;
        this.lossTimestamp = lossTimestamp;
        this.availableTimestamp = availableTimestamp;
    }
  public FactoryReportDTO(String productId, BigDecimal weight, LocalDate datetime, BigDecimal oee,
                            BigDecimal availability, BigDecimal performance, BigDecimal quality) {
        this.productId = productId;
        this.weight = weight;
        this.datetime = datetime;
        this.oee = oee;
        this.availability = availability;
        this.performance = performance;
        this.quality = quality;
    }
      public FactoryReportDTO(BigDecimal weight, LocalDate datetime, BigDecimal oee,
                            BigDecimal availability, BigDecimal performance, BigDecimal quality) {
        this.weight = weight;
        this.datetime = datetime;
        this.oee = oee;
        this.availability = availability;
        this.performance = performance;
        this.quality = quality;
    }
    // Getters and Setters can be added here if needed

    
} 

