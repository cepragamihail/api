package ro.kyosai.api.service;

import static ro.kyosai.api.domain.DonutChartItem.getAsProcentage;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ro.kyosai.api.domain.DonutChartItem;
import ro.kyosai.api.domain.ReportPerformanceRecord;
import ro.kyosai.api.repository.jdbc.FactoryLineReportJDBC;
import ro.kyosai.api.repository.jdbc.FactoryPerformanceReportJDBC;

@Service
public class PlaningDonutChartService {

    private static final Logger log = LoggerFactory.getLogger(PlaningDonutChartService.class);

    private final FactoryPerformanceReportJDBC factoryPerformanceReportJDBC;
    private final FactoryLineReportJDBC factoryLineReportJDBC;

    public PlaningDonutChartService(FactoryPerformanceReportJDBC factoryPerformanceReportJDBC, FactoryLineReportJDBC factoryLineReportJDBC) {
        this.factoryLineReportJDBC = factoryLineReportJDBC;
        this.factoryPerformanceReportJDBC = factoryPerformanceReportJDBC;
    }

    public List<DonutChartItem> getFactoryPlaningDonutChartBetweenDate(LocalDateTime start, LocalDateTime end) {
        var performanceSumBetween = factoryPerformanceReportJDBC.getPerformanceSumBetween(start, end);

        return getDonutChartItems(start, end, performanceSumBetween);
    }

    public List<DonutChartItem> getFactoryLinePlaningDonutChartBetweenDate(LocalDateTime start, LocalDateTime end) {
        var performanceSumBetween = factoryLineReportJDBC.getPerformanceSumBetween(start, end);

        return getDonutChartItems(start, end, performanceSumBetween);
    }

    private List<DonutChartItem> getDonutChartItems(LocalDateTime start, LocalDateTime end,
            List<ReportPerformanceRecord> performanceSumBetween) {
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

        return List.of(
                new DonutChartItem("Unplanned time", getAsProcentage(available, total), "#23B574"),
                new DonutChartItem("Production time", getAsProcentage(production, total), "#8CC63E"),
                new DonutChartItem("Stop loss", getAsProcentage(loss, total), "#29AAE3"));
    }

    
}
