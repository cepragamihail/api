package ro.kyosai.api.service;


import java.util.List;

import ro.kyosai.api.domain.FactoryReportChartDTO;

public interface FactoryReportService {

    FactoryReportChartDTO getFactoryReportChartsBetweenDate(String startDate, String endDate);
    List<?>  getFactoryLineReportsBetweenDate(String startDate, String endDate);
}
