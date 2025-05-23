package ro.kyosai.api.repository.jdbc;

import static ro.kyosai.api.utility.Utility.generateQueryByDbNameTableNameAndTableAlias;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import ro.kyosai.api.domain.FactoryReportDTO;
import ro.kyosai.api.utility.Utility;

@Component
public class FactoryLineReportJDBC {


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
        String query = generateQueryByDbNameTableNameAndTableAlias(tableNames.get(0), "t1", dbName);
        String queryBetweenDatesCaluse = Utility.getQueryWithWhereCaluseBetwenDates(query, "t1");
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

}
