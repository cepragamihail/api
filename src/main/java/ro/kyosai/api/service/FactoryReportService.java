package ro.kyosai.api.service;


import java.util.List;

import ro.kyosai.api.domain.FactoryReportChartDTO;
import ro.kyosai.api.domain.FactoryReportTotalsDTO;

public interface FactoryReportService {

    FactoryReportTotalsDTO getAllFactoryReportsSumBetweenDate(String startDate, String endDate);
    FactoryReportChartDTO getFactoryReportChartsBetweenDate(String startDate, String endDate);
    List<?>  getFactoryLineReportsBetweenDate(String startDate, String endDate);
}
