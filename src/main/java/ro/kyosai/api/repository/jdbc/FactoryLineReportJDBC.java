package ro.kyosai.api.repository.jdbc;

import static ro.kyosai.api.utility.FactoryReportColumn.AVAILABLE_TIMESTAMP;
import static ro.kyosai.api.utility.FactoryReportColumn.DATETIME;
import static ro.kyosai.api.utility.FactoryReportColumn.LOSS_TIMESTAMP;
import static ro.kyosai.api.utility.FactoryReportColumn.PRODUCTION_TIMESTAMP;
import static ro.kyosai.api.utility.FactoryReportColumn.getPerformanceColumnsEnum;
import static ro.kyosai.api.utility.Utility.SUM_ALIAS_PREFIX;
import static ro.kyosai.api.utility.Utility.generateQueryByDbNameTableNameAndTableAlias;
import static ro.kyosai.api.utility.Utility.getColumnsNameAndAliasWithSumFunctions;
import static ro.kyosai.api.utility.Utility.getTableNameWithAliasAndDBName;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ro.kyosai.api.domain.FactoryReportDTO;
import ro.kyosai.api.domain.ReportPerformanceRecord;
import ro.kyosai.api.utility.QueryBuilder;
import ro.kyosai.api.utility.Utility;

@Repository
public class FactoryLineReportJDBC {


    private static final String SUM = "Sum";

    private static final String DEFAULT_ALIAS_T1 = "t1";

    private static final Logger log = LoggerFactory.getLogger(FactoryReportJDBC.class);

    private final JdbcTemplate jdbcTemplate;


    @Value("#{'${app.datasource.table.names:SRR60_Reports}'.split(',')}")
    private List<String> tableNames;
    
    @Value("${app.datasource.dbname:Mittal_Reports}")
    private String dbName;

    public FactoryLineReportJDBC(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Method get all data from the unuin tables
    public List<FactoryReportDTO> getReprtsFromFirstIndexTableNamesBetweenDates(final LocalDateTime startDate, final LocalDateTime endDate) {
        String query = generateQueryByDbNameTableNameAndTableAlias(tableNames.get(0), DEFAULT_ALIAS_T1, dbName);
        String queryBetweenDatesCaluse = Utility.getQueryWithWhereCaluseBetwenDates(query, DEFAULT_ALIAS_T1, "datetime");
        log.info("Executing query: {}", queryBetweenDatesCaluse);
        log.info("Start date: {}, End date {}", startDate, endDate);
        
        return jdbcTemplate.query(queryBetweenDatesCaluse, (rs, rowNum) -> new FactoryReportDTO(
                rs.getString("productId"),
                rs.getBigDecimal("weight"),
                rs.getDate("datetime").toLocalDate(),
                rs.getBigDecimal("oee"),
                rs.getBigDecimal("availability"),
                rs.getBigDecimal("performance"),
                rs.getBigDecimal("quality"),
                rs.getString("productionTimestamp"),
                rs.getString("lossTimestamp"),
                rs.getString("availableTimestamp")
        ), startDate, endDate);
    }

     public List<ReportPerformanceRecord> getPerformanceSumBetween(final LocalDateTime startDate, final LocalDateTime endDate) {
        
        String[] columnsWithSumFunctions = getColumnsNameAndAliasWithSumFunctions(DEFAULT_ALIAS_T1, getPerformanceColumnsEnum());
        String tableNameWithDBName = getTableNameWithAliasAndDBName(dbName, tableNames.get(0), DEFAULT_ALIAS_T1);

        QueryBuilder queryBuilder = new QueryBuilder();
        String query = queryBuilder.select(columnsWithSumFunctions)
        .from(tableNameWithDBName)
        .where(DEFAULT_ALIAS_T1 + "." + DATETIME.getColumnName() + " BETWEEN ? AND ?").build();
        log.info("Executing query: {}", query);

        return jdbcTemplate.query(query, (rs, rowNum) -> new ReportPerformanceRecord(
                rs.getInt(AVAILABLE_TIMESTAMP.getAlias() + SUM_ALIAS_PREFIX ),
                rs.getInt(PRODUCTION_TIMESTAMP.getAlias() + SUM_ALIAS_PREFIX),
                rs.getInt(LOSS_TIMESTAMP.getAlias() + SUM_ALIAS_PREFIX)
        ), startDate, endDate);
     }
    

}
