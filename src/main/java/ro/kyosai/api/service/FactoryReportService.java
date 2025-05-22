package ro.kyosai.api.service;


import ro.kyosai.api.domain.FactoryReportChartDTO;
import ro.kyosai.api.domain.FactoryReportTotalsDTO;

public interface FactoryReportService {

    FactoryReportTotalsDTO getAllFactoryReportsSumBetweenDate(String startDate, String endDate);
    FactoryReportChartDTO getFactoryReportChartsBetweenDate(String startDate, String endDate);
}
