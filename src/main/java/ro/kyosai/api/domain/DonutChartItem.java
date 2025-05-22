package ro.kyosai.api.domain;

import java.math.BigInteger;

public record DonutChartItem(String name, BigInteger value, String color) {

    public DonutChartItem(String name, BigInteger value, String color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

}
