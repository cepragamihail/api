package ro.kyosai.api.utility;

public class QueryBuilder {

    private StringBuilder query;
    private String whereClause;
    private String orderByClause;
    private String limitClause;
    private String groupByClause;

    public QueryBuilder() {
        this.query = new StringBuilder();
        this.whereClause = "";
        this.orderByClause = "";
        this.limitClause = "";
        this.groupByClause = "";
    }
    public QueryBuilder(String initQuery) {
        this();
        query.append(initQuery);
    }
    public QueryBuilder(String tableName, String... columns) {
        this(tableName);
        select(columns);
    }
    public QueryBuilder select(String... columns) {
        query.append("SELECT ");
        if (columns.length == 0) {
            query.append("*");
        } else {
            for (String column : columns) {
                query.append(column).append(", ");
            }
            query.setLength(query.length() - 2); // Remove last comma and space
        }
        return this;
    }

    public QueryBuilder from(String tableName) {
        query.append(" FROM ").append(tableName);
        return this;
    }
    public QueryBuilder fromSubquery(String subquery) {
        query.append(" FROM (").append(subquery).append(") AS subquery");
        return this;
    }

    public QueryBuilder join(String tableName, String condition) {
        query.append(" JOIN ").append(tableName).append(" ON ").append(condition);
        return this;
    }

    public QueryBuilder unionAll() {
        query.append(" UNION ALL ");
        return this;
    }

    public QueryBuilder where(String condition) {
        if (!whereClause.isEmpty()) {
            whereClause += " AND ";
        } else {
            whereClause = " WHERE ";
        }
        whereClause += condition;
        return this;
    }
    public QueryBuilder orderBy(String column, String direction) {
        if (!orderByClause.isEmpty()) {
            orderByClause += ", ";
        } else {
            orderByClause = " ORDER BY ";
        }
        orderByClause += column + " " + direction;
        return this;
    }
    public QueryBuilder limit(int limit) {
        limitClause = " LIMIT " + limit;
        return this;
    }
    public QueryBuilder groupBy(String... columns) {
        if (columns.length > 0) {
            groupByClause = " GROUP BY ";
            for (String column : columns) {
                groupByClause += column + ", ";
            }
            groupByClause = groupByClause.substring(0, groupByClause.length() - 2); // Remove last comma and space
        }
        return this;
    }
    public String build() {
        StringBuilder finalQuery = new StringBuilder(query.toString());
        if (!whereClause.isEmpty()) {
            finalQuery.append(whereClause);
        }
        if (!groupByClause.isEmpty()) {
            finalQuery.append(groupByClause);
        }
        if (!orderByClause.isEmpty()) {
            finalQuery.append(orderByClause);
        }
        if (!limitClause.isEmpty()) {
            finalQuery.append(limitClause);
        }
        return finalQuery.toString();
    }

}
