package ro.kyosai.api.domain;

import ro.kyosai.api.domain.Interface.FactoryReportChart;

public record FactoryReportChartDTO(FactoryLineDTO factoryLineDTO) implements FactoryReportChart   {

}
