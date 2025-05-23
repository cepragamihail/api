package ro.kyosai.api.domain;

import java.math.BigInteger;
import java.util.List;

public record FactoryLineDTO(BigInteger id, String title, String shortTitle,
                          GuageChartDTO oee, List<BarChartItem> production, List<DonutChartItem> plannedTime) {

    public FactoryLineDTO(BigInteger id, String title, String shortTitle,
                       GuageChartDTO oee, List<BarChartItem> production, List<DonutChartItem> plannedTime) {
        this.id = id;
        this.title = title;
        this.shortTitle = shortTitle;
        this.oee = oee;
        this.production = production;
        this.plannedTime = plannedTime;
    }
    

}
