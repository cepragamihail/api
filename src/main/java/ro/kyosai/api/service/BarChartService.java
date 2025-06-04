package ro.kyosai.api.service;

import static java.time.format.DateTimeFormatter.ofPattern;
import static ro.kyosai.api.utility.Utility.TONE_UNITS;
import static ro.kyosai.api.utility.Utility.abbreviateNumber;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ro.kyosai.api.domain.BarChartItem;
import ro.kyosai.api.repository.jdbc.FactoryReportJDBC;

@Service
public class BarChartService {

    private static final Logger log = LoggerFactory.getLogger(BarChartService.class);


    private final FactoryReportJDBC factoryReportJDBC;
    public BarChartService(FactoryReportJDBC factoryReportJDBC) {
        this.factoryReportJDBC = factoryReportJDBC;
    }

        public List<BarChartItem> getFactoryProductionsBarChartBetweenDate(LocalDateTime start, LocalDateTime end) {
        List<BarChartItem> productionChart = new ArrayList<>();

        factoryReportJDBC.getFactoryReportWeightBetween(start, end)
                .forEach((date, value) -> productionChart.add(new BarChartItem(
                        date.format(ofPattern("dd")),
                        value.toBigInteger(),
                        abbreviateNumber(value.toBigInteger()),
                        "#296900",
                        TONE_UNITS)));
        log.info("Factory Productions Bar Chart: {}", productionChart.size());
        return productionChart;
    }

}
