package ro.kyosai.api.domain;

import java.math.BigInteger;
import java.util.List;

public record GuageChartDTO(String title, String shortTitle, BigInteger value, List<?> sectors, List<?> legend) { }
