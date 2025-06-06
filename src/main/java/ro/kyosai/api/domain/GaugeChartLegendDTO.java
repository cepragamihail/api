package ro.kyosai.api.domain;

import java.math.BigInteger;
import java.util.List;

public record GaugeChartLegendDTO(BigInteger value, List<?> sectors, String label, String shortLabel, String units) {
}
