package ro.kyosai.api.domain;

import java.math.BigInteger;

public record BarChartItem(String name, BigInteger value, String valueAlias, String color, String unit) {

    public BarChartItem(String name, BigInteger value, String valueAlias, String color, String unit) {
        this.name = name;
        this.value = value;
        this.valueAlias = valueAlias;
        this.color = color;
        this.unit = unit;
    }
}
