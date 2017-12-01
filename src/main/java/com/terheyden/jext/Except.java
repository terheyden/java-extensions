package com.terheyden.jext;

import java.util.function.Supplier;

/**
 * Exception helpers.
 */
@SuppressWarnings("unchecked")
public final class Except {

    private Except() {
        // Util class.
    }

    /**
     * Wrap checked exceptions into unchecked.
     */
    public static <E extends Throwable> void wrap(ThrowableRunner func) throws E {
        try {
            func.run();
        } catch (Throwable t) {
            throw (E) t;
        }
    }

    /**
     * Wrap checked exceptions into unchecked.
     */
    public static <E extends Throwable, T> T wrap(ThrowableSupplier<T> func) throws E {
        try {
            return func.get();
        } catch (Throwable t) {
            throw (E) t;
        }
    }

    /**
     * Ignore all exceptions thrown, and continue.
     */
    public static void ignore(ThrowableRunner func) {
        try {
            func.run();
        } catch (Throwable t) {
            // Ignore.
        }
    }

    /**
     * Ignore all exceptions thrown, and continue.
     */
    public static <T> T ignore(ThrowableSupplier<T> func) {
        try {
            return func.get();
        } catch (Throwable t) {
            // Ignore.
            return null;
        }
    }

    @FunctionalInterface
    public interface ThrowableRunner {
        void run() throws Throwable;
    }

    /**
     * A {@link Supplier} that can throw an exception.
     */
    @FunctionalInterface
    public interface ThrowableSupplier<T> {
        T get() throws Throwable;
    }
}
