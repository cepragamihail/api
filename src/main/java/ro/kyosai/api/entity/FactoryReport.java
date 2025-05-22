package ro.kyosai.api.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "Factory_Reports")
public class FactoryReport {

    // Define the fields of the FactoryReport entity
    @Id
    @Column(name = "productId")
    String productId;
    @Column(name = "weight")
    BigDecimal weight;
    @Column(name = "datetime")
    LocalDateTime datetime;
    @Column(name = "oee")
    BigDecimal oee;
    @Column(name = "availability")
    BigDecimal availability;
    @Column(name = "performance")
    BigDecimal performance;
    @Column(name = "quality")
    BigDecimal quality;

}
