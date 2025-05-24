package ro.kyosai.api.service.impl;

import static java.time.format.DateTimeFormatter.ofPattern;
import static ro.kyosai.api.utility.Utility.abbreviateNumber;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import ro.kyosai.api.domain.BarChartItem;
import ro.kyosai.api.domain.DonutChartItem;
import ro.kyosai.api.domain.FactoryReportChartDTO;
import ro.kyosai.api.domain.FactoryReportTotalsDTO;
import ro.kyosai.api.domain.GuageChartDTO;
import ro.kyosai.api.domain.GuageChartLegendDTO;
import ro.kyosai.api.entity.FactoryReport;
import ro.kyosai.api.repository.FactoryReportRepository;
import ro.kyosai.api.repository.jdbc.FactoryLineReportJDBC;
import ro.kyosai.api.repository.jdbc.FactoryPerformanceReportJDBC;
import ro.kyosai.api.repository.jdbc.FactoryReportJDBC;
import ro.kyosai.api.service.FactoryReportService;

@AllArgsConstructor
@Service
public class FactoryReportServiceImplementation implements FactoryReportService {

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String PERCENTAGE_UNITS = "%";
    private static final String PREFIX_PERFORMANCE = "P";
    private static final String PREFIX_AVAILABILITY = "A";
    private static final String PREFIX_QUALITY = "Q";
    private static final String PERFORMANCE = "Performance";
    private static final String AVAILABILITY = "Availability";
    private static final String QUALITY = "Quality";
    private static final String OEE = "OEE";
    private static final Logger log = LoggerFactory.getLogger(FactoryReportServiceImplementation.class);
    private final FactoryPerformanceReportJDBC factoryPerformanceReportJDBC;
    private final FactoryReportRepository factoryReportRepository;
    private final FactoryReportJDBC factoryReportJDBC;
    private final FactoryLineReportJDBC factoryLineReportJDBC;

    public List<FactoryReport> getAllFactoryReportsBetweenDate(String startDate, String endDate) {
        LocalDateTime start = this.parseOrGetDefaulStartDate(startDate);
        LocalDateTime end = this.parseOrGetDefaulEndDate(endDate);
        log.info("Start date: {}, End date {}", start, end);
        getAllFactoryReportsSumBetweenDate(startDate, endDate);
        return factoryReportRepository.findByDatetimeBetween(start, end);
    }

    private LocalDateTime parseOrGetDefaulEndDate(String endDate) throws DateTimeParseException {
        if (endDate == null || endDate.isEmpty()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    private LocalDateTime parseOrGetDefaulStartDate(String startDate) throws DateTimeParseException {
        if (startDate == null || startDate.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            return now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    public FactoryReportTotalsDTO getAllFactoryReportsSumBetweenDate(String startDate, String endDate) {
        LocalDateTime start = this.parseOrGetDefaulStartDate(startDate).minusMonths(2);
        LocalDateTime end = this.parseOrGetDefaulEndDate(endDate);
        log.info("Sum of Factory Reports between {} and {}", start, end);

        FactoryReportTotalsDTO factoryReportTotals = factoryReportJDBC.getFactoryReportTotalsBetween(start, end);

        log.info("OEE: {}, Quality: {}, Availability: {}, Performance: {}, Weight: {}",
                factoryReportTotals.oeeSum(), factoryReportTotals.qualitySum(), factoryReportTotals.availabilitySum(),
                factoryReportTotals.performanceSum(), factoryReportTotals.weightSum());
        return factoryReportTotals;
    }

    public FactoryReportChartDTO getFactoryReportChartsBetweenDate(String startDate, String endDate) {

        LocalDateTime start = this.parseOrGetDefaulStartDate(startDate).minusMonths(2);
        LocalDateTime end = this.parseOrGetDefaulEndDate(endDate);
        log.info("Start date: {}, End date {}", start, end);
        List<DonutChartItem> donutChart = getPlaningDonutChartBetweenDate(start, end);
        List<BarChartItem> barChartDTOs = getProductionsBarChartBetweenDate(start, end);
        GuageChartDTO oeeGuageChartDTO = getOEEGuageChartBetweenDate(start, end);
        log.info("OEE Guage Chart: {}", oeeGuageChartDTO);
        log.info("Bar Chart: {}", barChartDTOs);
        log.info("Donut Chart: {}", donutChart);

        return new FactoryReportChartDTO(
                BigInteger.valueOf(1),
                "Factory Report",
                "Factory Report",
                oeeGuageChartDTO,
                barChartDTOs,
                donutChart);
    }

    public List<DonutChartItem> getPlaningDonutChartBetweenDate(LocalDateTime start, LocalDateTime end) {
        var performanceSumBetween = factoryPerformanceReportJDBC.getPerformanceSumBetween(start, end);
        if (performanceSumBetween.isEmpty()) {
            log.info("No records found between {} and {}", start, end);
            return List.of();
        }
        var first = performanceSumBetween.getFirst();
        int available = first.availableTimestamp();
        int production = first.productionTimestamp();
        int loss = first.lossTimestamp();
        int total = available + production + loss;

        if (total == 0) {
            log.warn("Total time is zero between {} and {}", start, end);
            return List.of();
        }
        var availablePct = Math.round((available * 100.0) / total);
        var productionPct = Math.round((production * 100.0) / total);
        var lossPct = Math.round((loss * 100.0) / total);

        return List.of(
                new DonutChartItem("Unplanned time", BigInteger.valueOf(availablePct), "#23B574"),
                new DonutChartItem("Production time", BigInteger.valueOf(productionPct), "#8CC63E"),
                new DonutChartItem("Stop loss", BigInteger.valueOf(lossPct), "#29AAE3"));

    }

    public List<BarChartItem> getProductionsBarChartBetweenDate(LocalDateTime start, LocalDateTime end) {
        List<BarChartItem> productionChart = new ArrayList<>();

        factoryReportJDBC.getFactoryReportWeightBetween(start, end)
                .forEach((date, value) -> productionChart.add(new BarChartItem(
                        date.format(ofPattern("dd")),
                        value.toBigInteger(),
                        abbreviateNumber(value.toBigInteger()),
                        "#296900",
                        PERCENTAGE_UNITS)));
        log.info("Productions Bar Chart: {}", productionChart.size());
        return productionChart;
    }

    public GuageChartDTO getOEEGuageChartBetweenDate(LocalDateTime start, LocalDateTime end) {
        FactoryReportTotalsDTO factoryReportTotals = factoryReportJDBC.getFactoryReportTotalsBetween(start, end);

        List<GuageChartLegendDTO> guageChartLegends = List.of(
                this.getGuageChartLegendByMeasurementType(AVAILABILITY,
                        factoryReportTotals.availabilitySum().toBigInteger(), PERCENTAGE_UNITS),
                this.getGuageChartLegendByMeasurementType(PERFORMANCE,
                        factoryReportTotals.performanceSum().toBigInteger(), PERCENTAGE_UNITS),
                this.getGuageChartLegendByMeasurementType(QUALITY, factoryReportTotals.qualitySum().toBigInteger(),
                        PERCENTAGE_UNITS));

        return new GuageChartDTO(
                factoryReportTotals.oeeSum().toBigInteger(),
                this.getSectorByMasurementType(OEE),
                guageChartLegends);
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

    @Override
    public List<?> getFactoryLineReportsBetweenDate(String startDate, String endDate) {
        LocalDateTime start = this.parseOrGetDefaulStartDate(startDate).minusYears(1);
        LocalDateTime end = this.parseOrGetDefaulEndDate(endDate);
        
        return factoryLineReportJDBC.getReprtsFromFirstIndexTableNamesBetweenDates(start, end);
    }

}
