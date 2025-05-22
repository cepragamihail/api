package ro.kyosai.api.service;

import java.util.List;

import ro.kyosai.api.domain.FactoryReportTotalsDTO;
import ro.kyosai.api.entity.FactoryReport;

public interface FactoryReportService {

    List<FactoryReport> getAllFactoryReportsBetweenDate(String startDate, String endDate);
    FactoryReportTotalsDTO getAllFactoryReportsSumBetweenDate(String startDate, String endDate);

    void jdbcImplementationExample();

}
