package ro.kyosai.api.domain;

import java.math.BigInteger;
import java.util.List;

public record GuageChartLegendDTO(BigInteger value, List<?> sectors,  String label, String shortLabel, String units) {

    public GuageChartLegendDTO(BigInteger value, List<?> sectors, String label, String shortLabel, String units) {
        this.value = value;
        this.sectors = sectors;
        this.label = label;
        this.shortLabel = shortLabel;
        this.units = units;
    }

}
