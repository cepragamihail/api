package ro.kyosai.api.repository.jdbc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import ro.kyosai.api.domain.ReportPerformanceDTO;
import ro.kyosai.api.domain.ReportPerformanceRecord;

@AllArgsConstructor
@Component
public class FactoryPerformanceReportJDBC {
    private static final Logger log = LoggerFactory.getLogger(FactoryPerformanceReportJDBC.class);

    private static final String FACTORY_PERFORMANCE_REPORT = """
            SELECT fpr.productId, fpr.datetime, fpr.Available_Timestamp, fpr.Production_Timestamp, fpr.Loss_Timestamp
            FROM Mittal_Reports.dbo.Factory_Performance_Reports fpr
            """;

    final JdbcTemplate jdbcTemplate;

    public Optional<List<ReportPerformanceDTO>> getFactoryPerformanceReport() {
        log.info("Executing query: {}", FACTORY_PERFORMANCE_REPORT);
        return Optional.ofNullable(jdbcTemplate.query(FACTORY_PERFORMANCE_REPORT,
                (rs, rowNum) -> new ReportPerformanceDTO(
                        rs.getString("productId"),
                        rs.getDate("datetime").toLocalDate(),
                        rs.getInt("Available_Timestamp"),
                        rs.getInt("Production_Timestamp"),
                        rs.getInt("Loss_Timestamp"))));
    }

       public Optional<List<ReportPerformanceDTO>> getFactoryPerformanceReportBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        final String query = FACTORY_PERFORMANCE_REPORT + " WHERE fpr.datetime BETWEEN ? AND ?";
        log.info("Executing query: {}", query);
        return Optional.ofNullable(jdbcTemplate.query(query,
                (rs, rowNum) -> new ReportPerformanceDTO(
                        rs.getString("productId"),
                        rs.getDate("datetime").toLocalDate(),
                        rs.getInt("Available_Timestamp"),
                        rs.getInt("Production_Timestamp"),
                        rs.getInt("Loss_Timestamp")), startDate, endDate));
    }

    public Optional<List<ReportPerformanceRecord>> getFactoryPerformanceReportByProductId(final String productId) {
        final String query = FACTORY_PERFORMANCE_REPORT + " WHERE fpr.productId = ?";
        log.info("Executing query: {}", query);
        return Optional.ofNullable(jdbcTemplate.query(query,
                (rs, rowNum) -> new ReportPerformanceRecord(
                        rs.getString("productId"),
                        rs.getDate("datetime").toLocalDate(),
                        rs.getInt("Available_Timestamp"),
                        rs.getInt("Production_Timestamp"),
                        rs.getInt("Loss_Timestamp")), productId));
    }

}
