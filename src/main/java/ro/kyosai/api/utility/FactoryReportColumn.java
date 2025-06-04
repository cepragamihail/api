package ro.kyosai.api.utility;

public enum FactoryReportColumn {
    ID("id", "id"),
    DATETIME("[Datetime]", "datetime"),
    DIAMETER("Diameter", "diameter"),
    WIDTH("Width", "width"),
    HEIGHT("Height", "height"),
    THICKNESS("Thickness", "thickness"),
    LENGTH("[Length]", "length"),
    WEIGHT("Weight", "weight"),
    TUBES_PER_PACK("Tubes_Per_Pack", "tubesPerPack"),
    AVAILABLE_TIME("Available_Time", "availableTime"),
    PRODUCTION_TIME("Production_Time", "productionTime"),
    LOSS_TIME("Loss_Time", "lossTime"),
    AVAILABLE_TIMESTAMP("Available_Timestamp", "availableTimestamp"),
    PRODUCTION_TIMESTAMP("Production_Timestamp", "productionTimestamp"),
    LOSS_TIMESTAMP("Loss_Timestamp", "lossTimestamp"),
    TOTAL_LINEAR_METERS("Total_Linear_Meters", "totalLinearMeters"),
    GOOD_LINEAR_METERS("Good_Linear_Meters", "goodLinearMeters"),
    REJECTED_LINEAR_METERS("Rejected_Linear_Meters", "rejectedLinearMeters"),
    MEAN_SPEED("Mean_Speed", "meanSpeed"),
    IDEAL_SPEED("Ideal_Speed", "idealSpeed"),
    YIELD("Yield", "yield"),
    OEE("OEE", "oee"),
    SHIFT("Shift", "shift"),
    ACTUAL_TUBES_PER_PACK("Actual_Tubes_Per_Pack", "actualTubesPerPack"),
    AVAILABILITY("Availability", "availability"),
    PERFORMANCE("Performance", "performance"),
    QUALITY("Quality", "quality"),
    CONSUMED_ENERGY("Consumed_Energy", "consumedEnergy");

    private final String columnName;
    private final String alias;

    FactoryReportColumn(String columnName, String alias) {
        this.columnName = columnName;
        this.alias = alias;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getAlias() {
        return alias;
    }

    public String getColumnNameWithAlias() {
        return String.format("%s AS %s", columnName, alias);
    }

    public static final FactoryReportColumn[] getGuageAnalisisColumnsEnum() {
        return new FactoryReportColumn[]{
            PRODUCTION_TIMESTAMP,
            AVAILABLE_TIMESTAMP,
            MEAN_SPEED,
            IDEAL_SPEED,
            GOOD_LINEAR_METERS,
            TOTAL_LINEAR_METERS,
            WEIGHT
        };
    }
    public static final FactoryReportColumn[] getGuageAnalisisColumnsEnumWithDatetime() {
        return new FactoryReportColumn[]{
            DATETIME,
            PRODUCTION_TIMESTAMP,
            AVAILABLE_TIMESTAMP,
            MEAN_SPEED,
            IDEAL_SPEED,
            GOOD_LINEAR_METERS,
            TOTAL_LINEAR_METERS,
            WEIGHT
        };
    }

    public static final String[] getPerformanceColumns() {
        return new String[]{
            AVAILABLE_TIMESTAMP.getColumnName(),
            PRODUCTION_TIMESTAMP.getColumnName(),
            LOSS_TIMESTAMP.getColumnName()
        };
    }

    public static final FactoryReportColumn[] getPerformanceColumnsEnumWithDatetime() {
        return new FactoryReportColumn[]{
            DATETIME,
            AVAILABLE_TIMESTAMP,
            PRODUCTION_TIMESTAMP,
            LOSS_TIMESTAMP
        };
    }

    public static final FactoryReportColumn[] getPerformanceColumnsEnum() {
        return new FactoryReportColumn[]{
            AVAILABLE_TIMESTAMP,
            PRODUCTION_TIMESTAMP,
            LOSS_TIMESTAMP
        };
    }
    public static final String[] getProductUniqueColumns() {
        return new String[]{
            DIAMETER.getColumnName(),
            WIDTH.getColumnName(),
            HEIGHT.getColumnName(),
            LENGTH.getColumnName(),
            THICKNESS.getColumnName()
        };
    }
    
}
