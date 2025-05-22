package ro.kyosai.api.repository.jdbc;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ro.kyosai.api.domain.FactoryReportTotalsDTO;

@AllArgsConstructor
@Component
public class FactoryReportJDBC {

    private static final Logger logger = LoggerFactory.getLogger(FactoryReportJDBC.class);
    private static final String FACTORY_REPORT_TOTALS = """
            SELECT SUM(OEE) AS oee, SUM(Quality) AS quality, SUM(Availability) AS availability,
            SUM(Performance) AS performance, SUM(Weight) AS weight FROM Factory_Reports
            """;

    private final JdbcTemplate jdbcTemplate;

    public FactoryReportTotalsDTO getFactoryReportTotals() {
        logger.info("Executing query: {}", FACTORY_REPORT_TOTALS);

        return jdbcTemplate.queryForObject(FACTORY_REPORT_TOTALS, (rs, rowNum) -> new FactoryReportTotalsDTO(
                rs.getBigDecimal("oee"),
                rs.getBigDecimal("quality"),
                rs.getBigDecimal("availability"),
                rs.getBigDecimal("performance"),
                rs.getBigDecimal("weight")));
    }
    public FactoryReportTotalsDTO getFactoryReportTotalsBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        String sql = FACTORY_REPORT_TOTALS + "WHERE datetime BETWEEN ? AND ?";

        logger.info("Executing query: {}", sql);

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new FactoryReportTotalsDTO(
                rs.getBigDecimal("oee"),
                rs.getBigDecimal("quality"),
                rs.getBigDecimal("availability"),
                rs.getBigDecimal("performance"),
                rs.getBigDecimal("weight")), startDate, endDate);
    }

}
