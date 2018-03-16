package com.terheyden.jext;

import java.util.concurrent.TimeUnit;

/**
 * Sensible conversions.
 */
public final class Convert {

    private Convert() {
        // Util class.
    }

    public static int daysToMs(int days) {
        return longToInt(TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS));
    }

    public static int hoursToMs(int hours) {
        return longToInt(TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS));
    }

    public static int minsToMs(int mins) {
        return longToInt(TimeUnit.MILLISECONDS.convert(mins, TimeUnit.MINUTES));
    }

    public static int secsToMs(int secs) {
        return longToInt(TimeUnit.MILLISECONDS.convert(secs, TimeUnit.SECONDS));
    }

    public static int msToDays(long ms) {
        return longToInt(TimeUnit.DAYS.convert(ms, TimeUnit.MILLISECONDS));
    }

    public static int msToHours(long ms) {
        return longToInt(TimeUnit.HOURS.convert(ms, TimeUnit.MILLISECONDS));
    }

    public static int msToMins(long ms) {
        return longToInt(TimeUnit.MINUTES.convert(ms, TimeUnit.MILLISECONDS));
    }

    public static int msToSecs(long ms) {
        return longToInt(TimeUnit.SECONDS.convert(ms, TimeUnit.MILLISECONDS));
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
