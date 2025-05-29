package ro.kyosai.api.repository.jdbc;

import static ro.kyosai.api.utility.FactoryReportColumn.getPerformanceColumns;
import static ro.kyosai.api.utility.FactoryReportColumn.getPerformanceColumnsEnum;
import static ro.kyosai.api.utility.FactoryReportColumn.getPerformanceColumnsEnumWithDatetime;
import static ro.kyosai.api.utility.Utility.SUM_ALIAS_PREFIX;
import static ro.kyosai.api.utility.Utility.getColumnsNameWithTableAlias;
import static ro.kyosai.api.utility.Utility.getTableNameWithAliasAndDBName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ro.kyosai.api.domain.ReportPerformanceDTO;
import ro.kyosai.api.domain.ReportPerformanceRecord;
import ro.kyosai.api.utility.FactoryReportColumn;
import ro.kyosai.api.utility.QueryBuilder;
import ro.kyosai.api.utility.Utility;

@Component
public class FactoryPerformanceReportJDBC {
    private static final Logger log = LoggerFactory.getLogger(FactoryPerformanceReportJDBC.class);

        private static final String TABLE_ALIAS_PREFIX = "tar";


    private static final String FACTORY_PERFORMANCE_REPORT = """
            SELECT fpr.productId, fpr.datetime, fpr.Available_Timestamp, fpr.Production_Timestamp, fpr.Loss_Timestamp
            FROM Mittal_Reports.dbo.Factory_Performance_Reports fpr
            """;


    @Value("#{'${app.datasource.table.names:SRR60_Reports}'.split(',')}")
    private List<String> tableNames;
    
    @Value("${app.datasource.dbname:Mittal_Reports}")
    private String dbName;

    final JdbcTemplate jdbcTemplate;
        public FactoryPerformanceReportJDBC(JdbcTemplate jdbcTemplate) {
                this.jdbcTemplate = jdbcTemplate;
        }

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

public List<ReportPerformanceRecord> getPerformanceSumBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        String[] performanceColumns = getPerformanceColumns();
        String[] columnsWithSumFunctions = Utility.getColumnsWithSumFunctions(performanceColumns);

        String query = new QueryBuilder().select(columnsWithSumFunctions)
                .fromSubquery(getUnionAllTablePerformanceQuery(tableNames, dbName))
                .where( FactoryReportColumn.DATETIME.getColumnName() + " BETWEEN ? AND ? ")
                .build();

        for (FactoryReportColumn performanceColumnsEnum : getPerformanceColumnsEnum()) {
                log.info("Column: {}, Alias: {}", performanceColumnsEnum.getColumnName(), performanceColumnsEnum.getAlias());
        }
        return jdbcTemplate.query(query,
                (rs, rowNum) -> new ReportPerformanceRecord(
                        rs.getInt(FactoryReportColumn.AVAILABLE_TIMESTAMP.getColumnName() + SUM_ALIAS_PREFIX),
                        rs.getInt(FactoryReportColumn.PRODUCTION_TIMESTAMP.getColumnName() + SUM_ALIAS_PREFIX),
                        rs.getInt(FactoryReportColumn.LOSS_TIMESTAMP.getColumnName() + SUM_ALIAS_PREFIX)
                        ), startDate, endDate);
    }

    public String getUnionAllTablePerformanceQuery(List<String> tableNames, String dbName) {
        int[] idx = { 1 };
        return tableNames.stream()
                .map(table -> getPerfomanceQueryByTableName(table, TABLE_ALIAS_PREFIX + idx[0]++, dbName))
                .collect(Collectors.joining(" UNION ALL "));
    }


    public String getPerfomanceQueryByTableName(String tableName, String tableAlias, String dbName) {
        return new QueryBuilder()
                        .select(getColumnsNameWithTableAlias(tableAlias, getPerformanceColumnsEnumWithDatetime()))
                        .from(getTableNameWithAliasAndDBName(dbName, tableName, tableAlias))
                        .build();
    }

}
