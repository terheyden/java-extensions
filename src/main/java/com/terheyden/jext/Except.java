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

    ////////////////////////////////////////////////////////////////////////////////
    // THROWABLE RUNNER:

    @FunctionalInterface
    public interface ThrowableRunner {
        void run() throws Exception;
    }

    /**
     * Wrap checked exceptions into unchecked.
     */
    public static <E extends Exception> void wrap(ThrowableRunner func) throws E {
        try {
            func.run();
        } catch (Exception t) {
            throw (E) t;
        }
    }

    /**
     * Ignore all exceptions thrown, and continue.
     */
    public static void ignore(ThrowableRunner func) {
        try {
            func.run();
        } catch (Exception t) {
            // Ignore.
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // THROWABLE SUPPLIER:

    /**
     * A {@link Supplier} that can throw an exception.
     */
    @FunctionalInterface
    public interface ThrowableSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Wrap checked exceptions into unchecked.
     */
    public static <E extends Exception, T> T wrap(ThrowableSupplier<T> func) throws E {
        try {
            return func.get();
        } catch (Exception t) {
            throw (E) t;
        }
    }

    /**
     * Ignore all exceptions thrown, and continue.
     */
    public static <T> T ignore(ThrowableSupplier<T> func) {
        try {
            return func.get();
        } catch (Exception t) {
            // Ignore.
            return null;
        }
    }

    /**
     * Ignore all exceptions thrown, and continue. Return default value, if an exception occurs.
     */
    public static <T> T ignoreWithDefault(ThrowableSupplier<T> func, T defaultVal) {
        try {
            return func.get();
        } catch (Exception t) {
            return defaultVal;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // THROWABLE FUNCTION:

    @FunctionalInterface
    public interface ThrowableFunction<T, R> {
        R apply(T val) throws Exception;
    }
}
