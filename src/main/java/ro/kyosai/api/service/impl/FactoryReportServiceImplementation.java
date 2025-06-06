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
import ro.kyosai.api.domain.GaugeChartDTO;
import ro.kyosai.api.entity.FactoryReport;
import ro.kyosai.api.repository.FactoryReportRepository;
import ro.kyosai.api.repository.jdbc.FactoryLineReportJDBC;
import ro.kyosai.api.service.BarChartService;
import ro.kyosai.api.service.FactoryReportService;
import ro.kyosai.api.service.GaugeChartService;
import ro.kyosai.api.service.PlaningDonutChartService;

@AllArgsConstructor
@Service
public class FactoryReportServiceImplementation implements FactoryReportService {

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final Logger log = LoggerFactory.getLogger(FactoryReportServiceImplementation.class);

    private final PlaningDonutChartService planingDonutChartService;

    private final FactoryReportRepository factoryReportRepository;
    private final FactoryLineReportJDBC factoryLineReportJDBC;
    private final BarChartService barChartService;
    private final GaugeChartService gaugeChartService;

    public List<FactoryReport> getAllFactoryReportsBetweenDate(String startDate, String endDate) {
        LocalDateTime start = this.parseOrGetDefaultStartDate(startDate);
        LocalDateTime end = this.parseOrGetDefaultEndDate(endDate);
        log.info("Start date: {}, End date {}", start, end);
        return factoryReportRepository.findByDatetimeBetween(start, end);
    }

    private LocalDateTime parseOrGetDefaultEndDate(String endDate) throws DateTimeParseException {
        if (endDate == null || endDate.isEmpty()) {
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    private LocalDateTime parseOrGetDefaultStartDate(String startDate) throws DateTimeParseException {
        if (startDate == null || startDate.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            return now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    public FactoryReportChartDTO getFactoryReportChartsBetweenDate(String startDate, String endDate) {

        LocalDateTime start = this.parseOrGetDefaultStartDate(startDate).minusMonths(2);
        LocalDateTime end = this.parseOrGetDefaultEndDate(endDate);

        log.info("Start date: {}, End date {}", start, end);
        List<DonutChartItem> donutChart = planingDonutChartService.getFactoryPlaningDonutChartBetweenDate(start, end);
        List<BarChartItem> barChartDTOs =  barChartService.getFactoryProductionsBarChartBetweenDate(start, end);
        GaugeChartDTO oeeGuageChartDTO = gaugeChartService.getFactoryOEEGaugeChartBetweenDate(start, end);
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
        LocalDateTime start = this.parseOrGetDefaultStartDate(startDate).minusYears(1);
        LocalDateTime end = this.parseOrGetDefaultEndDate(endDate);

        factoryLineReportJDBC.getPerformanceSumBetween(start, end)
                .forEach(performance -> log.info("Performance: {}", performance));
        
        return factoryLineReportJDBC.getReprtsFromFirstIndexTableNamesBetweenDates(start, end);
    }

}
