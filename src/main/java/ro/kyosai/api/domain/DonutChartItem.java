package ro.kyosai.api.domain;

import java.math.BigInteger;

public record DonutChartItem(String name, BigInteger value, String color) {

    public static BigInteger getAsPercentage(final int value, final int total) {
        return BigInteger.valueOf(Math.round((value * 100.0) / total));
    }
}
