package ro.kyosai.api.utility;

import static java.util.stream.Collectors.joining;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.stream.Stream;

public final class Utility {


    public static final String SUM_ALIAS_PREFIX = "Sum";
    public static final String DELIMITER = "_";
    public static final String TABLE_ALIAS_PREFIX = "tar";
    public static final String PERCENTAGE_UNITS = "%";

    public static final String QUALITY = "quality";
    public static final String PERFORMANCE = "performance";
    public static final String AVAILABILITY = "availability";
    public static final String OEE = "oee";
    public static final String DATETIME = "datetime";
    public static final String WEIGHT = "weight";
    public static final String PRODUCT_ID = "productId";
    public static final String AVAILABLE_TIMESTAMP = "availableTimestamp";
    public static final String LOSS_TIMESTAMP = "lossTimestamp";
    public static final String PRODUCTION_TIMESTAMP = "productionTimestamp";

    private Utility() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static long CalculateAndRoundPercentages(int value, int total) throws ArithmeticException {
        return Math.round((value * 100.0) / total);
    }

    public static String abbreviateNumber(BigInteger number) throws ArithmeticException {
        final String[] suffixes = { "", "K", "M", "B", "T" };
        BigDecimal value = new BigDecimal(number);
        int magnitude = 0;
        final BigDecimal thousand = BigDecimal.valueOf(1000);

        while (value.abs().compareTo(thousand) >= 0 && magnitude < suffixes.length - 1) {
            value = value.divide(thousand);
            magnitude++;
        }

        // Show one decimal if not an integer (e.g., 1.2K)
        String formatted = value.stripTrailingZeros().scale() > 0
                ? String.format("%.1f", value)
                : String.format("%.0f", value);

        return formatted + suffixes[magnitude];
    }

    public static final String generateQueryByDbNameTableNameAndTableAlias(String tableName, String tableAlias,
            String dbName) {
        return String.format(
            """
                SELECT CONCAT(%1$s.Diameter,'_', %1$s.Width,'_', %1$s.Height,'_', %1$s.[Length],'_', %1$s.Thickness) as %2$s,
                %1$s.Weight as %3$s,
                %1$s.[Datetime] as %4$s,
                %1$s.OEE as %5$s,
                %1$s.Availability as %6$s,
                %1$s.Performance as %7$s,
                %1$s.Quality as %8$s,
                %1$s.Production_Timestamp as %9$s,
                %1$s.Loss_Timestamp as %10$s,
                %1$s.Available_Timestamp as %11$s
                FROM %13$s.dbo.%12$s %1$s
                """,
            tableAlias,
            PRODUCT_ID,
            WEIGHT,
            DATETIME,
            OEE,
            AVAILABILITY,
            PERFORMANCE,
            QUALITY,
            PRODUCTION_TIMESTAMP,
            LOSS_TIMESTAMP,
            AVAILABLE_TIMESTAMP,
            tableName,
            dbName
        );
    }

    public static final List<String> getAllQueriesForAllTablesWithUniqueAliases(List<String> tableNames,
            String dbName, String tableAliasPrefix) {
        int[] idx = { 1 };
        return tableNames.stream()
                .map(table -> generateQueryForDbNameTableNameAndTableAlias(table, tableAliasPrefix + idx[0]++, dbName))
                .toList();
    }
    

    public static final String generateQueryForDbNameTableNameAndTableAlias(String tableName, String tableAlias,
            String dbName) {
        return generateQueryByDbNameTableNameAndTableAlias(tableName, tableAlias, dbName);
    }

    public static String generateUnionOfQueries(List<String> queries) {
        return String.join(" UNION ALL ", queries);
    }

    public static final String getQueryWithWhereCaluseBetwenDates(String query, String tableAlias, String columnName) {
        return String.format("%s WHERE %s.%s BETWEEN ? AND ? ", query, tableAlias, columnName);
    }

    public static final String getProductIdColumnName(String tableAlias) {
        return "CONCAT(" + 
                Stream.of(FactoryReportColumn.getProductUniqueColumns())
                .map(column -> tableAlias + "." + column)
                .collect(joining(DELIMITER)) +
                ") as " + PRODUCT_ID;
    }
    public static final String[] getColumnsNameAndAliasWithSumFunctions(String alias, FactoryReportColumn... columns) {
        return Stream.of(columns)
                .map(column -> String.format("SUM(%s.%s) as %sSum", alias, column.getColumnName(), column.getAlias()))
                .toArray(String[]::new);
    }
    public static final String getColumnWithSumFunctions(String columnName, String columnAlias) {
        return String.format("SUM(%s) as %sSum", columnName, columnAlias);
    }
    public static final String[] getColumnsWithSumFunctions(String... columsName) {
        return Stream.of(columsName)
                .map(column -> getColumnWithSumFunctions(column, column))
                .toArray(String[]::new);
    }
    public static final String[] getColumnsWithSumFunctions(FactoryReportColumn... columns) {
        return Stream.of(columns)
                .map(column -> getColumnWithSumFunctions(column.getColumnName(), column.getAlias()))
                .toArray(String[]::new);
    }
    public static final String getColumnWhitsCastASDate(String columnName, String alias) {
        return String.format("CAST(%s AS DATE) AS %s", columnName, alias);
    }

    public static final String getTableNameWithAliasAndDBName(String dbName ,String tableName, String alias) {
        return String.format("%s.dbo.%s %s", dbName, tableName, alias);
    }
    public static final String[] getColumnsNameWithTableAlias(String tableAlias, String... columns) {
        return Stream.of(columns)
                .map(column -> tableAlias + "." + column)
                .toArray(String[]::new);
    }

    public static final String[] getColumnsNameWithTableAlias(String tableAlias, FactoryReportColumn... columns) {
        return Stream.of(columns)
                .map(column -> tableAlias + "." + column.getColumnName())
                .toArray(String[]:: new);
    }
}
