package ro.kyosai.api.domain;

import java.math.BigInteger;
import java.util.List;

import ro.kyosai.api.domain.Interface.FactoryReportChart;

public record FactoryReportChartDTO(BigInteger id, String title, String shortTitle,
                                    GaugeChartDTO oee, List<?> production, List<?> plannedTime) implements FactoryReportChart {
                            
    public FactoryReportChartDTO(BigInteger id, String title, String shortTitle,
                                 GaugeChartDTO oee, List<?> production, List<?> plannedTime) {
        this.id = id;
        this.title = title;
        this.shortTitle = shortTitle;
        this.oee = oee;
        this.production = production;
        this.plannedTime = plannedTime;
    }

}
