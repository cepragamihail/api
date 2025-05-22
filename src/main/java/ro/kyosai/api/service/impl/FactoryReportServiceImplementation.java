package ro.kyosai.api.service.impl;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import ro.kyosai.api.domain.FactoryReportTotalsDTO;
import ro.kyosai.api.domain.ReportPerformanceRecord;
import ro.kyosai.api.entity.FactoryReport;
import ro.kyosai.api.repository.FactoryReportRepository;
import ro.kyosai.api.repository.jdbc.FactoryPerformanceReportJDBC;
import ro.kyosai.api.repository.jdbc.FactoryReportJDBC;
import ro.kyosai.api.service.FactoryReportService;

@AllArgsConstructor
@Service
public class FactoryReportServiceImplementation implements FactoryReportService {

    private static final Logger log = LoggerFactory.getLogger(FactoryReportServiceImplementation.class);
    private final FactoryPerformanceReportJDBC factoryPerformanceReportJDBC;
    private final FactoryReportRepository factoryReportRepository;
    private final FactoryReportJDBC factoryReportJDBC;

    @Override
    public List<FactoryReport> getAllFactoryReportsBetweenDate(String startDate, String endDate) {
    LocalDateTime start = this.parseOrGetDefaulStartDate(startDate); 
    LocalDateTime end = this.parseOrGetDefaulEndDate(endDate);
    log.info("Start date: {}, End date {}", start, end);
    getAllFactoryReportsSumBetweenDate(startDate, endDate);
    return factoryReportRepository.findByDatetimeBetween(start, end);
    }

    private LocalDateTime parseOrGetDefaulEndDate(String endDate) throws DateTimeParseException {
        if (endDate == null || endDate.isEmpty()) {
            // Return the current date and time
            return LocalDateTime.now();
        }
        // Assuming the date format is "yyyy-MM-dd HH:mm:ss"
        // You can adjust the format according to your needs.
        return LocalDateTime.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private LocalDateTime parseOrGetDefaulStartDate(String startDate) throws DateTimeParseException {
        if (startDate == null || startDate.isEmpty()) {
            // Return the first day of the current month at 00:00:00
            LocalDateTime now = LocalDateTime.now();
            return now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        }
        // Assuming the date format is "yyyy-MM-dd HH:mm:ss"
        // You can adjust the format according to your needs
        return LocalDateTime.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @Override
    public void jdbcImplementationExample() {

        factoryPerformanceReportJDBC.getFactoryPerformanceReportByProductId("0.00_100_100_6.015_4.00").ifPresentOrElse(
            reportDTOs -> {
                for (ReportPerformanceRecord dto : reportDTOs) {
                    log.info("Product ID: {}, DateTime: {}, Available Timestamp: {}, Production Timestamp: {}, Loss Timestamp: {}",
                        dto.productId(), dto.datetime(), dto.availableTimestamp(), dto.productionTimestamp(), dto.lossTimestamp());
                }
            },
            () -> log.info("No data found")
        );
    }

    @Override
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
}
