package com.terheyden.jext;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A better return value or {@link Optional} value,
 * a generic result from any method call.
 */
@ParametersAreNonnullByDefault
public final class OldResult {

    /**
     * The status of the result, either an Ok or fail.
     */
    public enum Status {
        Ok,
        Fail
    }

    private Status status;
    private String msg;
    private Object result;

    private OldResult(Status status) {
        this.status = status;
    }

    public OldResult setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public OldResult setResult(Object result) {
        this.result = result;
        return this;
    }

    @SuppressWarnings("unchecked")
    public <K, V> OldResult putResult(K key, V result) {

        Map<K, V> map = (Map<K, V>) this.result;

        if (map == null) {
            map = new HashMap<>();

            // If [this.result] was set to something not our map, it will get overwritten here.
            this.result = map;
        }

        map.put(key, result);
        return this;
    }

    /**
     * Create an Ok result.
     */
    public static @NotNull OldResult ok() {
        return new OldResult(Status.Ok);
    }

    /**
     * Create a fail result.
     */
    public static @NotNull OldResult fail() {
        return new OldResult(Status.Fail);
    }

    public static @NotNull OldResult okFailIfNull(@Nullable Object res) {

        if (res == null) {
            return fail();
        }

        return ok().setResult(res);
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

    /**
     * Returns true if the result status is Ok, otherwise false.
     */
    public boolean toBool() {
        return status == Status.Ok ? true : false;
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

    public boolean hasResult() {
        return result != null;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // SINGLE OBJ RESULT:

    @SuppressWarnings("unchecked")
    public @Nullable <T> T getResult() {
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T getResultOrDefault(T defaultVal) {
        return result != null ? (T) result : defaultVal;
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T getResultOrThrow(String throwMsg) throws Exception {

        if (result == null) {
            throw new Exception(throwMsg);
        }

        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public @Nullable <T> T getResultOrThrow() throws Exception {

        if (result == null) {
            throw new Exception("Non-null value was expected.");
        }

        return (T) result;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // MAP RESULT:

    @SuppressWarnings("unchecked")
    public @Nullable <K, V> V getResult(K key) {

        Map<K, V> map = (Map<K, V>) result;
        if (map == null) {
            return null;
        }

        return map.get(key);
    }

    @SuppressWarnings("unchecked")
    public <K, V> V getResultOrDefault(K key, V defaultVal) {

        Map<K, V> map = (Map<K, V>) result;
        if (map == null) {
            return null;
        }

        return map.getOrDefault(key, defaultVal);
    }

    @SuppressWarnings("unchecked")
    public <K, V> V getResultOrThrow(K key, String throwMsg) throws Exception {

        Map<K, V> map = (Map<K, V>) result;
        if (map == null) {
            return null;
        }

        if (!map.containsKey(key)) {
            throw new Exception(throwMsg);
        }

        return map.get(key);
    }

    public <K, V> V getResultOrThrow(K key) throws Exception {
        return getResultOrThrow(key, "Non-null value was expected.");
    }
}
