package ro.kyosai.api.service;

import static ro.kyosai.api.domain.FactoryProductionReportDTO.getAsProcentage;
import static ro.kyosai.api.domain.FactoryProductionReportDTO.getOEEProcentage;
import static ro.kyosai.api.utility.Utility.PERCENTAGE_UNITS;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ro.kyosai.api.domain.FactoryProductionReportDTO;
import ro.kyosai.api.domain.GaugeChartDTO;
import ro.kyosai.api.domain.GaugeChartLegendDTO;
import ro.kyosai.api.repository.jdbc.FactoryReportJDBC;

@Service
public class GaugeChartService {

    private static final String PREFIX_PERFORMANCE = "P";
    private static final String PREFIX_AVAILABILITY = "A";
    private static final String PREFIX_QUALITY = "Q";
    private static final String PERFORMANCE = "Performance";
    private static final String AVAILABILITY = "Availability";
    private static final String QUALITY = "Quality";
    private static final String OEE = "OEE";

    Logger log = LoggerFactory.getLogger(GaugeChartService.class);

    private final FactoryReportJDBC factoryReportJDBC;

    public GaugeChartService(FactoryReportJDBC factoryReportJDBC) {
        this.factoryReportJDBC = factoryReportJDBC;
    }

    public GaugeChartDTO getFactoryOEEGaugeChartBetweenDate(LocalDateTime start, LocalDateTime end) {
        FactoryProductionReportDTO factoryProductionReport = factoryReportJDBC
                .getFactoryReportOfGuageAnalisisChartBetween(start, end);
        BigDecimal availability = getAsProcentage(
                factoryProductionReport.productionTimestamp(),
                factoryProductionReport.availableTimestamp());
        BigDecimal performance = getAsProcentage(
                factoryProductionReport.meanSpeed(),
                factoryProductionReport.idealSpeed());
        BigDecimal quality = getAsProcentage(
                factoryProductionReport.goodLinearMeters(),
                factoryProductionReport.totalLinearMeters());
        BigDecimal oee = getOEEProcentage(availability, performance, quality);
        List<GaugeChartLegendDTO> gaugeChartLegends = List.of(
                this.getGaugeChartLegendByMeasurementType(AVAILABILITY, availability.toBigInteger(), PERCENTAGE_UNITS),
                this.getGaugeChartLegendByMeasurementType(PERFORMANCE, performance.toBigInteger(), PERCENTAGE_UNITS),
                this.getGaugeChartLegendByMeasurementType(QUALITY, quality.toBigInteger(), PERCENTAGE_UNITS));

        return new GaugeChartDTO(
                OEE,
                OEE,
                oee.toBigInteger(),
                this.getSectorByMeasurementType(OEE),
                gaugeChartLegends);
    }

    public GaugeChartLegendDTO getGaugeChartLegendByMeasurementType(String measurementType, BigInteger value,
                                                                    String units) {
        switch (measurementType) {
            case OEE:
                return new GaugeChartLegendDTO(value, this.getSectorByMeasurementType(OEE), OEE, OEE, units);
            case QUALITY:
                return new GaugeChartLegendDTO(value, this.getSectorByMeasurementType(QUALITY), QUALITY, PREFIX_QUALITY,
                        units);
            case AVAILABILITY:
                return new GaugeChartLegendDTO(value, this.getSectorByMeasurementType(AVAILABILITY), AVAILABILITY,
                        PREFIX_AVAILABILITY, units);
            case PERFORMANCE:
                return new GaugeChartLegendDTO(value, this.getSectorByMeasurementType(PERFORMANCE), PERFORMANCE,
                        PREFIX_PERFORMANCE, units);
            default:
                throw new IllegalArgumentException("Invalid measurement type: " + measurementType);
        }
    }

    public List<Integer> getSectorByMeasurementType(String measurementType) {
        return switch (measurementType) {
            case OEE -> List.of(30, 55, 65, 100);
            case QUALITY -> List.of(85, 90, 95, 100);
            case AVAILABILITY -> List.of(30, 55, 65, 100);
            case PERFORMANCE -> List.of(60, 75, 85, 100);
            default -> throw new IllegalArgumentException("Invalid measurement type: " + measurementType);
        };
    }

}
