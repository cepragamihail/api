package ro.kyosai.api.service.impl;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import ro.kyosai.api.entity.FactoryReport;
import ro.kyosai.api.repository.FactoryReportRepository;
import ro.kyosai.api.repository.jdbc.FactoryLineReportJDBC;
import ro.kyosai.api.repository.jdbc.FactoryReportJDBC;
import ro.kyosai.api.service.BarChartService;
import ro.kyosai.api.service.FactoryReportService;
import ro.kyosai.api.service.GuageChartService;
import ro.kyosai.api.service.PlaningDonutChartService;

@AllArgsConstructor
@Service
public class FactoryReportServiceImplementation implements FactoryReportService {

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final Logger log = LoggerFactory.getLogger(FactoryReportServiceImplementation.class);

    private final PlaningDonutChartService planingDonutChartService;

    private final FactoryReportRepository factoryReportRepository;
    private final FactoryReportJDBC factoryReportJDBC;
    private final FactoryLineReportJDBC factoryLineReportJDBC;
    private final BarChartService barChartService;
    private final GuageChartService guageChartService;

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

        FactoryReportTotalsDTO factoryReportTotals = factoryReportJDBC.getFactoryReportOfGuageAnalisisChartBetween(start, end);

        log.info("OEE: {}, Quality: {}, Availability: {}, Performance: {}, Weight: {}",
                factoryReportTotals.oeeSum(), factoryReportTotals.qualitySum(), factoryReportTotals.availabilitySum(),
                factoryReportTotals.performanceSum(), factoryReportTotals.weightSum());
        return factoryReportTotals;
    }

    public FactoryReportChartDTO getFactoryReportChartsBetweenDate(String startDate, String endDate) {

        LocalDateTime start = this.parseOrGetDefaulStartDate(startDate).minusMonths(2);
        LocalDateTime end = this.parseOrGetDefaulEndDate(endDate);
        log.info("Start date: {}, End date {}", start, end);
        List<DonutChartItem> donutChart = planingDonutChartService.getFactoryPlaningDonutChartBetweenDate(start, end);
        List<BarChartItem> barChartDTOs =  barChartService.getFactoryProductionsBarChartBetweenDate(start, end);
        GuageChartDTO oeeGuageChartDTO = guageChartService.getFactoryOEEGuageChartBetweenDate(start, end);
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


    @Override
    public List<?> getFactoryLineReportsBetweenDate(String startDate, String endDate) {
        LocalDateTime start = this.parseOrGetDefaulStartDate(startDate).minusYears(1);
        LocalDateTime end = this.parseOrGetDefaulEndDate(endDate);

        factoryLineReportJDBC.getPerformanceSumBetween(start, end)
                .forEach(performance -> log.info("Performance: {}", performance));
        
        return factoryLineReportJDBC.getReprtsFromFirstIndexTableNamesBetweenDates(start, end);
    }

}
