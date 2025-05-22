package ro.kyosai.api.repository.jdbc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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
                SUM(Performance) AS performance, SUM(Weight) AS weight
            FROM Factory_Reports
            """;
    private final JdbcTemplate jdbcTemplate;

    public FactoryReportTotalsDTO getFactoryReportTotals() {

        logger.info("Executing query: {}", FACTORY_REPORT_TOTALS);

        return jdbcTemplate.queryForObject(FACTORY_REPORT_TOTALS, (rs, rowNum) -> new FactoryReportTotalsDTO(
                rs.getBigDecimal("oee"),
                rs.getBigDecimal("quality"),
                rs.getBigDecimal("availability"),
                rs.getBigDecimal("performance"),
                rs.getBigDecimal("weight")
                ));
    }
    public FactoryReportTotalsDTO getFactoryReportTotalsBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        String sql = String.format("%s WHERE datetime BETWEEN ? AND ?", FACTORY_REPORT_TOTALS);
        logger.info("Executing query: {}", sql);

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new FactoryReportTotalsDTO(
                rs.getBigDecimal("oee"),
                rs.getBigDecimal("quality"),
                rs.getBigDecimal("availability"),
                rs.getBigDecimal("performance"),
                rs.getBigDecimal("weight")
                ), startDate, endDate);
    } 

    public Map<LocalDate, BigDecimal> getFactoryReportWeightBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        String sql = """
                    SELECT CAST(datetime AS DATE) AS day, SUM(weight) AS weight
                        FROM Factory_Reports
                        WHERE datetime BETWEEN ? AND ?
                        GROUP BY CAST(datetime AS DATE)
                        ORDER BY day 
                    """;

        logger.info("Executing query: {}", sql);

        return jdbcTemplate.query(sql, rs -> {
            Map<LocalDate, BigDecimal> result = new java.util.HashMap<>();
            while (rs.next()) {
                LocalDate day = rs.getDate("day").toLocalDate();
                BigDecimal weight = rs.getBigDecimal("weight");
                result.put(day, weight);
            }
            return result;
        }, startDate, endDate);
    }

}
