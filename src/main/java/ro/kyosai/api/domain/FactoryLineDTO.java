package ro.kyosai.api.domain;

import java.math.BigInteger;

public record FactoryLineDTO(BigInteger id, String title, String shortTitle,
                          GuageChartDTO oee, BarChartDTO production, DonutChart plannedTime) {

    public FactoryLineDTO( BigInteger id, String title, String shortTitle,
                       GuageChartDTO oee, BarChartDTO production, DonutChart plannedTime) {
        this.id = id;
        this.title = title;
        this.shortTitle = shortTitle;
        this.oee = oee;
        this.production = production;
        this.plannedTime = plannedTime;
    }
    

}
