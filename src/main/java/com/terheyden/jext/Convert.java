package com.terheyden.jext;

import java.util.concurrent.TimeUnit;

/**
 * Sensible conversions.
 */
public final class Convert {

    private Convert() {
        // Util class.
    }

    public static int daysToMs(int years) {
        return longToInt(TimeUnit.MILLISECONDS.convert(years, TimeUnit.DAYS));
    }

    public static int longToInt(long num) {

        if (num >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }

        if (num <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }

        return Math.toIntExact(num);
    }
}
