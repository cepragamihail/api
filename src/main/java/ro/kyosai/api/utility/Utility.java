package ro.kyosai.api.utility;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Utility {


    public static long CalculateAndRoundPercentages(int value, int total) throws ArithmeticException {
        return Math.round((value * 100.0) / total);
    }

    public static String abbreviateNumber(BigInteger number) throws ArithmeticException {
        final String[] suffixes = {"", "K", "M", "B", "T"};
        BigDecimal value = new BigDecimal(number);
        int magnitude = 0;
        final BigDecimal thousand = BigDecimal.valueOf(1000);

        while (value.abs().compareTo(thousand) >= 0 && magnitude < suffixes.length - 1) {
            value = value.divide(thousand);
            magnitude++;
        }

        // Show one decimal if not an integer (e.g., 1.2K)
        String formatted = value.stripTrailingZeros().scale() > 0
                ? String.format("%.1f", value)
                : String.format("%.0f", value);

        return formatted + suffixes[magnitude];
    }


}
