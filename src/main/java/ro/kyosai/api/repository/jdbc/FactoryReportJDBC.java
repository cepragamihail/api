package ro.kyosai.api.repository.jdbc;

import static ro.kyosai.api.utility.FactoryReportColumn.AVAILABILITY;
import static ro.kyosai.api.utility.FactoryReportColumn.DATETIME;
import static ro.kyosai.api.utility.FactoryReportColumn.OEE;
import static ro.kyosai.api.utility.FactoryReportColumn.PERFORMANCE;
import static ro.kyosai.api.utility.FactoryReportColumn.QUALITY;
import static ro.kyosai.api.utility.FactoryReportColumn.WEIGHT;
import static ro.kyosai.api.utility.FactoryReportColumn.getGuageAnalisisColumnsEnum;
import static ro.kyosai.api.utility.FactoryReportColumn.getGuageAnalisisColumnsEnumWithDatetime;
import static ro.kyosai.api.utility.Utility.SUM_ALIAS_PREFIX;
import static ro.kyosai.api.utility.Utility.TABLE_ALIAS_PREFIX;
import static ro.kyosai.api.utility.Utility.getColumnWhitsCastASDate;
import static ro.kyosai.api.utility.Utility.getColumnsNameWithTableAlias;
import static ro.kyosai.api.utility.Utility.getTableNameWithAliasAndDBName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ro.kyosai.api.domain.FactoryReportTotalsDTO;
import ro.kyosai.api.utility.FactoryReportColumn;
import ro.kyosai.api.utility.QueryBuilder;
import ro.kyosai.api.utility.Utility;

@Repository
public class FactoryReportJDBC {

    private static final String DAY_ALIAS = "day";

    private static final Logger log = LoggerFactory.getLogger(FactoryReportJDBC.class);

    @Value("#{'${app.datasource.table.names:SRR60_Reports}'.split(',')}")
    private List<String> tableNames;
    
    @Value("${app.datasource.dbname:Mittal_Reports}")
    private String dbName;

    private final JdbcTemplate jdbcTemplate;

    public FactoryReportJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public FactoryReportTotalsDTO getFactoryReportOfGuageAnalisisChartBetween(final LocalDateTime startDate, final LocalDateTime endDate) {

        String unionAllQuery = this.getUnionAllTableQuery(tableNames, dbName, getGuageAnalisisColumnsEnumWithDatetime());
        String[] guageAnalisisSumColumns = Utility.getColumnsWithSumFunctions(getGuageAnalisisColumnsEnum());
        String query = new QueryBuilder().select(guageAnalisisSumColumns)
            .fromSubquery(unionAllQuery)
            .where(DATETIME.getColumnName() + " BETWEEN ? AND ?")
            .build();
        log.info("Executing query: {}", query);
        return jdbcTemplate.queryForObject(query, (rs, rowNum) -> new FactoryReportTotalsDTO(
            rs.getBigDecimal(OEE.getAlias() + SUM_ALIAS_PREFIX),
            rs.getBigDecimal(QUALITY.getAlias() + SUM_ALIAS_PREFIX),
            rs.getBigDecimal(AVAILABILITY.getAlias() + SUM_ALIAS_PREFIX),
            rs.getBigDecimal(PERFORMANCE.getAlias() + SUM_ALIAS_PREFIX),
            rs.getBigDecimal(WEIGHT.getAlias() + SUM_ALIAS_PREFIX)
        ), startDate, endDate);
    }

    public Map<LocalDate, BigDecimal> getFactoryReportWeightBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        String unionAllQuery = this.getUnionAllTableQuery(tableNames, dbName, DATETIME, WEIGHT);
        String datetimeWithCastColumn = getColumnWhitsCastASDate(DATETIME.getColumnName(), DAY_ALIAS);
        String weightSumColumns = Utility.getColumnWithSumFunctions(WEIGHT.getColumnName(), WEIGHT.getAlias());
        // Add the datetime column with cast to 

        String query = new QueryBuilder().select(datetimeWithCastColumn, weightSumColumns)
            .fromSubquery(unionAllQuery)
            .where(DATETIME.getColumnName() + " BETWEEN ? AND ?")
            .groupBy(String.format("CAST(%s AS DATE)", DATETIME.getColumnName()))
            .orderBy(DAY_ALIAS, "ASC")
            .build();
        log.info("Executing query: {}", query);
        return jdbcTemplate.query(query, rs -> {
            Map<LocalDate, BigDecimal> result = new java.util.HashMap<>();
            while (rs.next()) {
                LocalDate day = rs.getDate(DAY_ALIAS).toLocalDate();
                BigDecimal weight = rs.getBigDecimal(WEIGHT.getAlias() + SUM_ALIAS_PREFIX);
                result.put(day, weight);
            }
            return result;
        }, startDate, endDate);
    }

    public String getUnionAllTableQuery(List<String> tableNames, String dbName, FactoryReportColumn... columns) {
        int[] idx = { 1 };
        return tableNames.stream()
                .map(table -> getQueryByTableName(table, TABLE_ALIAS_PREFIX + idx[0]++, dbName, columns))
                .collect(Collectors.joining(" UNION ALL "));
    }

    public String getQueryByTableName(String tableName, String tableAlias, String dbName, FactoryReportColumn... columns) {
        return new QueryBuilder()
                        .select(getColumnsNameWithTableAlias(tableAlias, columns))
                        .from(getTableNameWithAliasAndDBName(dbName, tableName, tableAlias))
                        .build();
    }

}
