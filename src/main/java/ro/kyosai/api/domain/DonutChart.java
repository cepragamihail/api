package ro.kyosai.api.domain;

import java.math.BigInteger;

public record DonutChart(String name, BigInteger value, String color) {

    public DonutChart(String name, BigInteger value, String color) {
        this.name = name;
        this.value = value;
        this.color = color;
    }

}
