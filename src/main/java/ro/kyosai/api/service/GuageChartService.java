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
import ro.kyosai.api.domain.GuageChartDTO;
import ro.kyosai.api.domain.GuageChartLegendDTO;
import ro.kyosai.api.repository.jdbc.FactoryReportJDBC;

@Service
public class GuageChartService {

    private static final String PREFIX_PERFORMANCE = "P";
    private static final String PREFIX_AVAILABILITY = "A";
    private static final String PREFIX_QUALITY = "Q";
    private static final String PERFORMANCE = "Performance";
    private static final String AVAILABILITY = "Availability";
    private static final String QUALITY = "Quality";
    private static final String OEE = "OEE";

    Logger log = LoggerFactory.getLogger(GuageChartService.class);

    private final FactoryReportJDBC factoryReportJDBC;

    public GuageChartService(FactoryReportJDBC factoryReportJDBC) {
        this.factoryReportJDBC = factoryReportJDBC;
    }

    public GuageChartDTO getFactoryOEEGuageChartBetweenDate(LocalDateTime start, LocalDateTime end) {
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
        List<GuageChartLegendDTO> guageChartLegends = List.of(
                this.getGuageChartLegendByMeasurementType(AVAILABILITY, availability.toBigInteger(), PERCENTAGE_UNITS),
                this.getGuageChartLegendByMeasurementType(PERFORMANCE, performance.toBigInteger(), PERCENTAGE_UNITS),
                this.getGuageChartLegendByMeasurementType(QUALITY, quality.toBigInteger(), PERCENTAGE_UNITS));

        return new GuageChartDTO(
                oee.toBigInteger(),
                this.getSectorByMasurementType(OEE),
                guageChartLegends);
    }

    public GuageChartLegendDTO getGuageChartLegendByMeasurementType(String measurementType, BigInteger value,
            String units) {
        switch (measurementType) {
            case OEE:
                return new GuageChartLegendDTO(value, this.getSectorByMasurementType(OEE), OEE, OEE, units);
            case QUALITY:
                return new GuageChartLegendDTO(value, this.getSectorByMasurementType(QUALITY), QUALITY, PREFIX_QUALITY,
                        units);
            case AVAILABILITY:
                return new GuageChartLegendDTO(value, this.getSectorByMasurementType(AVAILABILITY), AVAILABILITY,
                        PREFIX_AVAILABILITY, units);
            case PERFORMANCE:
                return new GuageChartLegendDTO(value, this.getSectorByMasurementType(PERFORMANCE), PERFORMANCE,
                        PREFIX_PERFORMANCE, units);
            default:
                throw new IllegalArgumentException("Invalid measurement type: " + measurementType);
        }
    }

    public List<Integer> getSectorByMasurementType(String measurementType) {
        switch (measurementType) {
            case OEE:
                return List.of(30, 55, 65, 100);
            case QUALITY:
                return List.of(85, 90, 95, 100);
            case AVAILABILITY:
                return List.of(30, 55, 65, 100);
            case PERFORMANCE:
                return List.of(60, 75, 85, 100);
            default:
                throw new IllegalArgumentException("Invalid measurement type: " + measurementType);
        }
    }

}
