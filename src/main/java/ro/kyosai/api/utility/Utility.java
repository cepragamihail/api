package ro.kyosai.api.utility;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public final class Utility {

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

    public static final String generateQueryByDbNameTableNameAndTableAlias(String tableName, String tableAlias, String dbName) {
        return String.format("""
            SELECT CONCAT(%1$s.Diameter,'_', %1$s.Width,'_', %1$s.Height,'_', %1$s.[Length],'_', %1$s.Thickness) as productId,
            %1$s.Weight as weight,
            %1$s.[Datetime] as datetime,
            %1$s.OEE as oee,
            %1$s.Availability as availability,
            %1$s.Performance as performance,
            %1$s.Quality as quality,
            %1$s.Production_Timestamp as productionTimestamp,
            %1$s.Loss_Timestamp as lossTimestamp,
            %1$s.Available_Timestamp as availableTimestamp
            FROM %3$s.dbo.%2$s %1$s
            """, tableAlias, tableName, dbName);
    }

    public static final List<String> getAllQueriesForAllTablesWithUniqueAliases(List<String> tableNames, String dbName) {
        int[] idx = {1};
        return tableNames.stream()
            .map(table -> generateQueryForDbNameTableNameAndTableAlias(table, idx[0]++, dbName))
            .toList();
    }

    public static final String generateQueryForDbNameTableNameAndTableAlias(String tableName, int aliasIndex, String dbName) {
        return generateQueryByDbNameTableNameAndTableAlias(tableName, "sr" + aliasIndex, dbName); // Use a unique alias based on the index
    }

    public static String generateUnionOfQueries(List<String> queries) {
        return String.join(" UNION ALL ", queries);
    }

    public static final String getQueryWithWhereCaluseBetwenDates(String query, String tableAlias) {
        return String.format("%s WHERE %s.datetime BETWEEN ? AND ? ", query, tableAlias);
    }

}
