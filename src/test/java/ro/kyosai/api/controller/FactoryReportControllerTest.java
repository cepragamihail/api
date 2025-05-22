package ro.kyosai.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.springframework.http.MediaType;

import static org.mockito.BDDMockito.given;

import ro.kyosai.api.domain.FactoryReportTotalsDTO;
import ro.kyosai.api.service.FactoryReportService;

@WebMvcTest(FactoryReportController.class)
public class FactoryReportControllerTest {


    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    FactoryReportService factoryReportService;

    @Test
    public void testGetAllFactoryReports() throws Exception {
        // Perform a GET request to the endpoint
        mockMvc.perform(get("/api/v1/factory-reports"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    public void testGetAllFactoryReportsWithDateRange() throws Exception {
        // Perform a GET request to the endpoint with date range
        mockMvc.perform(get("/api/v1/factory-reports")
                .param("start", "2023-01-01")
                .param("end", "2023-12-31"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getFactoryReportTotals_thenReturnFactoryReportTotals() throws Exception {
        // Arrange: create a mock DTO
        FactoryReportTotalsDTO mockTotals = new FactoryReportTotalsDTO(
            BigDecimal.valueOf(100),
            BigDecimal.valueOf(90),
            BigDecimal.valueOf(80),
            BigDecimal.valueOf(70),
            BigDecimal.valueOf(60)
        );

        given(factoryReportService.getAllFactoryReportsSumBetweenDate("2023-01-01", "2023-12-31"))
            .willReturn(mockTotals);

        // Act & Assert
        mockMvc.perform(get("/api/v1/factory-reports/totals")
                .param("start", "2023-01-01")
                .param("end", "2023-12-31"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.oeeSum").value(100))
            .andExpect(jsonPath("$.qualitySum").value(90))
            .andExpect(jsonPath("$.availabilitySum").value(80))
            .andExpect(jsonPath("$.performanceSum").value(70))
            .andExpect(jsonPath("$.weightSum").value(60));
    }


}
