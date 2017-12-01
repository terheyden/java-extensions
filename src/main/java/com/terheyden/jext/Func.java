package com.terheyden.jext;

import java.util.function.Supplier;

/**
 * Function and method utils.
 */
@SuppressWarnings("unchecked")
public final class Func {

    private Func() {
        // Util class.
    }




    ////////////////////////////////////////////////////////////////////////////////
    // OMG TOO COMPLICATED:

    /**
     * Build a custom {@link ThrowableRunner}.
     */
    public static RunnerBuilder quietly(ThrowableRunner func) {
        return new RunnerBuilder(func);
    }

    /**
     * Build a {@link ThrowableSupplier} with customizations.
     */
    public static <T> SupplierBuilder<T> quietly(ThrowableSupplier<T> func) {
        return new SupplierBuilder<>(func);
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

    /**
     * Build a {@link ThrowableSupplier} with customizations.
     */
    public static class SupplierBuilder<T> {

        private final ThrowableSupplier<T> func;
        private boolean ignoreExceptions;
        private T defaultVal;
        private String throwMsg;

        SupplierBuilder(ThrowableSupplier<T> func) {
            this.func = func;
        }

        public SupplierBuilder<T> ignoreExceptions() {
            ignoreExceptions = true;
            return this;
        }

        /**
         * Set a default value to return in case of null, or exception, if ignoring exceptions.
         */
        public SupplierBuilder<T> defaultValue(T defaultVal) {
            this.defaultVal = defaultVal;
            return this;
        }

        public SupplierBuilder<T> throwMsg(String throwMsg) {
            this.throwMsg = throwMsg;
            return this;
        }

        /**
         * Execute your Supplier func and return.
         * Makes all checked exceptions unchecked, which complies with functional programming.
         */
        public <E extends Throwable> T get() throws E {
            try {

                // Run the supplier.
                // Use the default value if the supplier returns null.
                T res = func.get();
                return res == null ? defaultVal : res;

            } catch (Throwable t) {

                // They can choose to ignore exceptions.
                if (!ignoreExceptions) {

                    // Wrap the exception if they have a custom msg.
                    if (throwMsg != null) {
                        throw new IllegalStateException(throwMsg, t);
                    }

                    throw (E) t;
                }

                // If an exception occurred, and we're ignoring exceptions,
                // and there's a default value, use that.
                return defaultVal;
            }
        }
    }

    public static class RunnerBuilder {

        private final ThrowableRunner func;
        private boolean ignoreExceptions;
        private String throwMsg;

        RunnerBuilder(ThrowableRunner func) {
            this.func = func;
        }

        public RunnerBuilder ignoreExceptions() {
            ignoreExceptions = true;
            return this;
        }

        public RunnerBuilder throwMsg(String throwMsg) {
            this.throwMsg = throwMsg;
            return this;
        }

        /**
         * Execute your func.
         * Makes all checked exceptions unchecked, which complies with functional programming.
         */
        public <E extends Throwable> void run() throws E {
            try {

                func.run();

            } catch (Throwable t) {

                // They can choose to ignore exceptions.
                if (!ignoreExceptions) {

                    // Wrap the exception if they have a custom msg.
                    if (throwMsg != null) {
                        throw new IllegalStateException(throwMsg, t);
                    }

                    throw (E) t;
                }
            }
        }
    }
}
