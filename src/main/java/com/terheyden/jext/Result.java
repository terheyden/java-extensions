package com.terheyden.jext;

import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * Represent the result of a method call or work item.
 * Immutable and Serializable.
 * A Result can be Ok / Fail.
 * A Result can have a message and/or a value.
 */
@ParametersAreNonnullByDefault
public final class Result<T> implements Serializable {

    /**
     * The status of the result, either an Ok or Fail.
     */
    public enum Status {
        Unknown,
        Ok,
        Fail
    }

    private final Status status;
    private String msg;
    private T value;

    /**
     * Create a new Result with Unknown status.
     * Should only be used during de-serializing.
     */
    private Result() {
        status = Status.Unknown;
    }

    /**
     * Create a new fully-custom Result.
     * @param status the resulting state of some action
     * @param msg an optional message to return with the result state
     * @param resultObj an optional result object to return
     */
    private Result(Status status, @Nullable T resultObj, @Nullable String msg) {
        this.status = status;
        this.msg = msg;
        this.value = resultObj;
    }

    public static <T> Result<T> ok(T resultVal, String resultMsg) {
        return new Result<>(Status.Ok, resultVal, resultMsg);
    }

    public static <T> Result<T> ok(T resultVal) {
        return new Result<>(Status.Ok, resultVal, null);
    }

    public static <T> Result<T> ok() {
        return new Result<>(Status.Ok, null, null);
    }

    public static <T> Result<T> fail(String resultMsg) {
        return new Result<>(Status.Fail, null, resultMsg);
    }

    public static <T> Result<T> fail() {
        return new Result<>(Status.Fail, null, null);
    }

    public static <T> Result<T> okIfNotNull(@Nullable T resultVal) {

        if (resultVal == null) {
            return fail();
        }

        return ok(resultVal);
    }

    /**
     * Returns true if the result was Ok.
     */
    public boolean isOk() {
        return status == Status.Ok;
    }

    /**
     * Returns true if the result was a fail.
     */
    public boolean isFail() {
        return status == Status.Fail;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public boolean hasMsg() {
        return msg != null;
    }

    public @Nullable String getMsg() {
        return msg;
    }

    public String getMsgOrDefault(String defaultMsg) {
        return this.msg == null ? defaultMsg : this.msg;
    }

    public Result<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public boolean hasValue() {
        return value != null;
    }

    public @Nullable T getValue() {
        return value;
    }

    public T getValueOrDefault(T defaultVal) {
        return value != null ? value : defaultVal;
    }

    public T getValueOrThrow(String throwMsg) throws Exception {

        if (value == null) {
            throw new Exception(throwMsg);
        }

        return value;
    }

    public T getValueOrThrow() throws Exception {

        if (value == null) {
            throw new Exception("Non-null value was expected.");
        }

        return value;
    }

    public T getValueOrThrow(Exception e) throws Exception {

        if (value == null) {
            throw e;
        }

        return value;
    }

    public Stream<T> resultStream() {

        if (value == null) {
            return Stream.empty();
        }

        return Stream.of(value);
    }
}
