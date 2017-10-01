package com.terheyden.jext;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Value (vars and args) validation utils.
 */
@SuppressWarnings("unchecked")
public class Val {

    private static final String   EMPTY_STRING  = "";
    private static final int      EMPTY_INT     = Integer.MIN_VALUE;
    private static final long     EMPTY_LONG    = Long.MIN_VALUE;
    private static final float    EMPTY_FLOAT   = Float.MIN_VALUE;
    private static final double   EMPTY_DOUBLE  = Double.MIN_VALUE;
    private static final boolean  EMPTY_BOOLEAN = false;
    private static final Object[] EMPTY_ARRAY   = new Object[0];
    private static final File     EMPTY_FILE    = new File("");
    private static final Path     EMPTY_PATH    = Paths.get("");

    private Val() {
        // Util class.
    }

    ////////////////////////////////////////////////////////////////////////////////
    // NULL:

    public static boolean isNull(Object val) {
        return val == null;
    }

    public static boolean notNull(Object val) {
        return !isNull(val);
    }

    public static <T> T orIfNull(T val, T useIfNull) {
        return val == null ? useIfNull : val;
    }

    public static <T> T throwIfNull(T val, String errMsg, Object... errMsgArgs) {

        if (isNull(val)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return val;
    }

    public static <T> T nsafe(T val) {
        if (val == null) throw new NullPointerException();
        return val;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // EMPTY:

    /**
     * Returns true if the string is null, an empty string, or contains only blank characters.
     */
    public static boolean isEmpty(String val) {

        if (val == null || val.trim().isEmpty()) {
            return true;
        }

        return false;
    }

    public static boolean isEmpty(Map<?, ?> val) {
        return val == null || val.isEmpty();
    }

    public static boolean isEmpty(Collection<?> val) {
        return val == null || val.isEmpty();
    }

    public static boolean isEmpty(Object[] val) {
        return val == null || val.length == 0;
    }

    public static boolean anyEmpty(String... vals) {

        for (String val : vals) {
            if (isEmpty(val)) {
                return true;
            }
        }

        return false;
    }

    public static boolean allEmpty(String... vals) {

        for (String val : vals) {
            if (notEmpty(val)) {
                return false;
            }
        }

        return true;
    }

    public static boolean notEmpty(String val) {
        return !isEmpty(val);
    }

    public static boolean notEmpty(Map<?, ?> val) {
        return !isEmpty(val);
    }

    public static boolean notEmpty(Collection<?> val) {
        return !isEmpty(val);
    }

    public static boolean notEmpty(Object[] val) {
        return !isEmpty(val);
    }

    public static boolean anyNotEmpty(String... vals) {

        for (String val : vals) {
            if (notEmpty(val)) {
                return true;
            }
        }

        return false;
    }

    public static boolean allNotEmpty(String... vals) {

        for (String val : vals) {
            if (isEmpty(val)) {
                return false;
            }
        }

        return true;
    }

    public static String orIfEmpty(String val, String useIfEmpty) {
        return isEmpty(val) ? useIfEmpty : val;
    }

    public static <T extends Map<?, ?>> T orIfEmpty(T map, T useIfEmpty) {
        return isEmpty(map) ? useIfEmpty : map;
    }

    public static <T extends Collection<?>> T orIfEmpty(T coll, T useIfEmpty) {
        return isEmpty(coll) ? useIfEmpty : coll;
    }

    public static <T> T[] orIfEmpty(T[] arr, T[] useIfEmpty) {
        return isEmpty(arr) ? useIfEmpty : arr;
    }

    public static String throwIfEmpty(String val, String errMsg, Object... errMsgArgs) {

        if (isEmpty(val)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return val;
    }

    public static <T extends Map<?, ?>> T throwIfEmpty(T val, String errMsg, Object... errMsgArgs) {

        if (isEmpty(val)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return val;
    }

    public static <T extends Collection<?>> T throwIfEmpty(T val, String errMsg, Object... errMsgArgs) {

        if (isEmpty(val)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return val;
    }

    public static <T> T[] throwIfEmpty(T[] val, String errMsg, Object... errMsgArgs) {

        if (isEmpty(val)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return val;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // EXISTS (FILES AND PATHS):

    public static boolean exists(Path path) {
        return notNull(path) && Files.exists(path);
    }

    public static boolean exists(File file) {
        return notNull(file) && file.exists();
    }

    public static boolean notExists(Path path) {
        return isNull(path) || Files.notExists(path);
    }

    public static boolean notExists(File file) {
        return isNull(file) || !file.exists();
    }

    public static Path throwIfExists(Path path, String errMsg, Object... errMsgArgs) {

        if (exists(path)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return path;
    }

    public static File throwIfExists(File file, String errMsg, Object... errMsgArgs) {

        if (exists(file)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return file;
    }

    public static Path throwNotExists(Path path, String errMsg, Object... errMsgArgs) {

        if (notExists(path)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return path;
    }

    public static File throwNotExists(File file, String errMsg, Object... errMsgArgs) {

        if (notExists(file)) {
            throw new IllegalStateException(
                isEmpty(errMsgArgs) ?
                    errMsg :
                    String.format(errMsg, errMsgArgs)
            );
        }

        return file;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // SAFE:

    public static String safe(String val) {
        return orIfNull(val, emptyString());
    }

    public static Integer safe(Integer val) {
        return orIfNull(val, emptyInt());
    }

    public static Long safe(Long val) {
        return orIfNull(val, emptyLong());
    }

    public static Float safe(Float val) {
        return orIfNull(val, emptyFloat());
    }

    public static Double safe(Double val) {
        return orIfNull(val, emptyDouble());
    }

    public static Boolean safe(Boolean val) {
        return orIfNull(val, emptyBoolean());
    }

    public static <T extends Collection<?>> T safe(T val) {
        return orIfNull(val, emptyCollection());
    }

    public static <T extends List<?>> T safe(T val) {
        return orIfNull(val, emptyList());
    }

    public static <T extends Set<?>> T safe(T val) {
        return orIfNull(val, emptySet());
    }

    public static <T extends Map<?, ?>> T safe(T val) {
        return orIfNull(val, emptyMap());
    }

    public static <T> T[] safe(T[] val) {
        return orIfNull(val, emptyArray());
    }

    public static Object safe(Object val) {
        return orIfNull(val, emptyObject());
    }

    public static File safe(File val) {
        return orIfNull(val, emptyFile());
    }

    public static Path safe(Path val) {
        return orIfNull(val, emptyPath());
    }

    ////////////////////////////////////////////////////////////////////////////////
    // EMPTY VALUES:

    public static String emptyString() {
        return EMPTY_STRING;
    }

    public static int emptyInt() {
        return EMPTY_INT;
    }

    public static long emptyLong() {
        return EMPTY_LONG;
    }

    public static float emptyFloat() {
        return EMPTY_FLOAT;
    }

    public static double emptyDouble() {
        return EMPTY_DOUBLE;
    }

    public static boolean emptyBoolean() {
        return EMPTY_BOOLEAN;
    }

    public static <T extends Collection<?>> T emptyCollection() {
        return (T) Collections.emptyList();
    }

    public static <T extends List<?>> T emptyList() {
        return (T) Collections.emptyList();
    }

    public static <T extends Set<?>> T emptySet() {
        return (T) Collections.emptySet();
    }

    public static <T extends Map<?, ?>> T emptyMap() {
        return (T) Collections.emptyMap();
    }

    public static <T> T[] emptyArray() {
        return (T[]) EMPTY_ARRAY;
    }

    public static Object emptyObject() {
        return EMPTY_STRING;
    }

    public static File emptyFile() {
        return EMPTY_FILE;
    }

    public static Path emptyPath() {
        return EMPTY_PATH;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // STRINGS:

    public static boolean equals(Object a, Object b) {

        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a == b) {
            return true;
        }

        return a.equals(b);
    }

    public static boolean contains(String text, String findStr) {

        // Check for equality first.
        if (equals(text, findStr)) {
            return true;
        }

        // An empty text definitely doesn't contain your str.
        if (text == null || text.isEmpty()) {
            return false;
        }

        // If your str is empty, it's technically contained.
        if (findStr == null || findStr.isEmpty()) {
            return true;
        }

        return text.contains(findStr);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // EXPERIMENTAL:

    public static GuardChain guard(boolean ifTrue, String thenThrow) {

        GuardChain guardChain = new GuardChain();
        return guardChain.guard(ifTrue, thenThrow);
    }

    public static class GuardChain {

        private GuardChain() {
            // Create only via the guard() method.
        }

        public GuardChain guard(boolean ifTrue, String thenThrow) {
            if (ifTrue) {
                throw new IllegalArgumentException(thenThrow);
            }

            return this;
        }

    }

}

/*

        if (Val.isEmpty(nsafe(urlVerificationReq).challenge)) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        if (urlVerificationReq == null || urlVerificationReq.challenge == null || urlVerificationReq.challenge.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

 */