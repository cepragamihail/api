package ro.kyosai.api.domain;

import java.math.BigInteger;
import java.util.List;

public record GuageChartDTO(BigInteger value, List<?> sectors, List<?> legend) {
    public GuageChartDTO(BigInteger value, List<?> sectors, List<?> legend) {
        this.value = value;
        this.sectors = sectors;
        this.legend = legend;
    }
}
