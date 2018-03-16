package com.terheyden.jext;

import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * String-related helper methods.
 */
@ParametersAreNonnullByDefault
public final class Str {

    private Str() {
        // Utils class.
    }

    public static @NotNull String fmt(String fmtText, Object... args) {

        if (Val.isEmpty(args)) {
            return fmtText;
        }

        return String.format(fmtText, args);
    }
}
