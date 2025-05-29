package ro.kyosai.api.domain;

import java.math.BigInteger;

public record DonutChartItem(String name, BigInteger value, String color) {

    public DonutChartItem(String name, BigInteger value, String color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

    public static final BigInteger getAsProcentage(final int value, final int total) {
        return BigInteger.valueOf(Math.round((value * 100.0) / total));
    }
}
