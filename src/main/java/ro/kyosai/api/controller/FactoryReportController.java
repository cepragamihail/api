package ro.kyosai.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import ro.kyosai.api.domain.FactoryReportTotalsDTO;
import ro.kyosai.api.service.FactoryReportService;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/factory-reports")
public class FactoryReportController {

    final FactoryReportService factoryReportService;

    @GetMapping
    public ResponseEntity<?> getAllFactoryReports(
            @RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate){
        return ResponseEntity.ok(factoryReportService.getFactoryReportChartsBetweenDate(startDate, endDate));
    }

    @GetMapping("/totals")
    public ResponseEntity<FactoryReportTotalsDTO> getAllFactoryReportsSum(
            @RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate){
                
        return ResponseEntity.ok(factoryReportService.getAllFactoryReportsSumBetweenDate(startDate, endDate));
    }

    @GetMapping("/lines")
    public ResponseEntity<?> getFactoryLineReports(
            @RequestParam(value = "start", required = false) String startDate,
            @RequestParam(value = "end", required = false) String endDate){
        return ResponseEntity.ok(factoryReportService.getFactoryLineReportsBetweenDate(startDate, endDate));
    }
}
