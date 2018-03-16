package com.terheyden.jext;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Value (vars and args) validation utils.
 */
@SuppressWarnings("unchecked")
public class Val {

    private static final String EMPTY_STRING = "";
    private static final int EMPTY_INT = Integer.MIN_VALUE;
    private static final long EMPTY_LONG = Long.MIN_VALUE;
    private static final float EMPTY_FLOAT = Float.MIN_VALUE;
    private static final double EMPTY_DOUBLE = Double.MIN_VALUE;
    private static final boolean EMPTY_BOOLEAN = false;
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private static final File EMPTY_FILE = new File("");
    private static final Path EMPTY_PATH = Paths.get("");

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

    public static <T> T throwIfNull(T obj, String errMsg, Object... errMsgArgs) {

        if (notNull(obj)) {
            return obj;
        }

        if (isNull(errMsg)) {
            errMsg = "Unexpected null value.";
        }

        throw new IllegalStateException(
            isEmpty(errMsgArgs) ? errMsg : String.format(errMsg, errMsgArgs)
        );
    }

    public static <T> T throwIfNull(T obj) {
        return throwIfNull(obj, null);
    }

    public static boolean anyNull(Object... objs) {

        if (objs == null || objs.length == 0) {
            return false;
        }

        for (Object obj : objs) {
            if (obj == null) {
                return true;
            }
        }

        return false;
    }

    public static boolean allNull(Object... objs) {

        if (objs == null || objs.length == 0) {
            return true;
        }

        for (Object obj : objs) {
            if (obj != null) {
                return false;
            }
        }

        return true;
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

    /**
     * Returns the first arg that is not empty (null or empty).
     */
    public static String firstNotEmpty(String val, String useIfEmpty) {
        return isEmpty(val) ? useIfEmpty : val;
    }

    /**
     * Returns the first arg that is not empty (null or empty).
     */
    public static <T extends Map<?, ?>> T firstNotEmpty(T map, T useIfEmpty) {
        return isEmpty(map) ? useIfEmpty : map;
    }

    /**
     * Returns the first arg that is not empty (null or empty).
     */
    public static <T extends Collection<?>> T firstNotEmpty(T coll, T useIfEmpty) {
        return isEmpty(coll) ? useIfEmpty : coll;
    }

    /**
     * Returns the first arg that is not empty (null or empty).
     */
    public static <T> T[] firstNotEmpty(T[] arr, T[] useIfEmpty) {
        return isEmpty(arr) ? useIfEmpty : arr;
    }

    /**
     * Throws an {@link IllegalStateException} if any of the args are null or invalid, based on their type.
     * Strings can't be empty, collections / maps / iterables can't be empty, files / paths must be valid.
     */
    private static boolean anyInvalid(boolean throwInsteadOfReturning, Object... args) {

        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Why are you calling anyInvalid() with no arguments?");
        }

        for (int ind = 0; ind < args.length; ind++) {

            Object arg = args[ind];

            if (isInvalid(arg)) {

                if (throwInsteadOfReturning) {
                    throw new IllegalArgumentException(String.format("Arg %d:%d is invalid: '%s'",
                        ind + 1,
                        args.length,
                        arg == null ? "(null)" : arg.toString()
                    ));
                }

                return true;
            }
        }

        // None are invalid!
        return false;
    }

    /**
     * Throws an {@link IllegalStateException} if any of the args are null or invalid, based on their type.
     * Strings can't be empty, collections / maps / iterables can't be empty, files / paths must be valid.
     */
    public static boolean anyInvalid(Object... args) {
        return anyInvalid(false, args);
    }

    public static boolean isInvalid(Object arg) {

        // Validate what "invalid" is based on type.

        if (arg == null) {

            return true;

        } else if (arg instanceof Path) {

            // Path is an Iterable, so this check goes way up here.
            if (Files.notExists((Path) arg)) {
                return true;
            }

        } else if (arg instanceof File) {

            if (!((File) arg).exists()) {
                return true;
            }

        } else if (arg instanceof Collection<?>) {

            if (((Collection<?>) arg).isEmpty()) {
                return true;
            }

        } else if (arg instanceof Iterable) {

            Iterable<?> iter = (Iterable<?>) arg;
            if (!iter.iterator().hasNext()) {
                return true;
            }

        } else if (arg.getClass().isArray()) {

            if (Array.getLength(arg) == 0) {
                return true;
            }

        } else if (arg instanceof Map<?, ?>) {

            if (((Map<?, ?>) arg).isEmpty()) {
                return true;
            }

        } else if (arg instanceof String) {

            if (isEmpty((String) arg)) {
                return true;
            }
        }

        // Everything looks good.
        return false;
    }

    public static void throwIfAnyInvalid(Object... args) {
        anyInvalid(true, args);
    }

    public static boolean isValid(Object obj) {
        return !isInvalid(obj);
    }

    public static boolean allValid(Object... args) {

        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("Don't call allValid() with zero args.");
        }

        return !anyInvalid(args);
    }

    private static final int MAX_DEPTH_DEBUG = 1;

    public static String toDebugStr(Object obj) {
        return debug(obj, 0);
    }

    /**
     * Simple toString() on any object. Will try to glean as much info as possible.
     */
    private static String debug(Object... args) {

        if (args == null || args.length == 0) {
            return "null";
        }

        int len = args.length;

        if (len == 1) {
            return debug(args[0], 0);
        }

        List<String> parts = new LinkedList<>();

        for (int i = 0; i < len; i++) {

            parts.add(String.format("%d/%d=%s",
                i + 1,
                len,
                debug(args[i], 0)));
        }

        return String.join("|", parts);
    }

    private static String debug(Object arg, int depth) {

        // Don't go too deep.
        boolean maxDeep = depth >= MAX_DEPTH_DEBUG;

        String argClassName = arg == null ? "null" : arg.getClass().getSimpleName();

        // Format based on type.

        if (arg == null) {

            return "null";

        } else if (arg instanceof Path) {

            // Path is an Iterable, so this check goes way up here.
            return ((Path) arg).toAbsolutePath().toString();

        } else if (arg instanceof File) {

            return ((File) arg).getAbsolutePath();

        } else if (arg instanceof Collection<?>) {

            Collection<?> coll = (Collection<?>) arg;

            if (coll.isEmpty()) {
                return "[]";
            }

            if (maxDeep) {

                return String.format("[%s:%d]", argClassName, coll.size());

            } else {

                List<String> elems = coll.stream()
                    .map(c -> debug(c, depth + 1))
                    .collect(Collectors.toList());

                return String.format("[%s:%d=%s]",
                    argClassName,
                    coll.size(),
                    String.join(",", elems));
            }

        } else if (arg instanceof Iterable) {

            Iterable<?> iter = (Iterable<?>) arg;
            if (!iter.iterator().hasNext()) {
                return "[]";
            }

            if (maxDeep) {

                int size = 0;

                for (Object o : iter) {
                    size++;
                }

                return String.format("[%s:%d]", argClassName, size);

            } else {

                List<String> elems = new LinkedList<>();

                int size = 0;

                for (Object o : iter) {
                    size++;
                    elems.add(debug(o, depth + 1));
                }

                return String.format("[%s:%d=%s]",
                    argClassName,
                    size,
                    String.join(",", elems));
            }

        } else if (arg.getClass().isArray()) {

            int len = Array.getLength(arg);

            if (len == 0) {
                return "[]";
            }

            if (maxDeep) {

                return String.format("[%s:%d]", argClassName, len);

            } else {

                List<String> elems = new LinkedList<>();

                for (int i = 0; i < len; i++) {
                    elems.add(debug(Array.get(arg, i), depth + 1));
                }

                return String.format("[%s:%d=%s]",
                    argClassName,
                    len,
                    String.join(",", elems));
            }

        } else if (arg instanceof Map<?, ?>) {

            Map<?, ?> map = (Map<?, ?>) arg;

            if (map.isEmpty()) {
                return "[]";
            }

            if (maxDeep) {

                return String.format("[%s:%d]", argClassName, map.size());

            } else {

                List<String> elems = new LinkedList<>();

                for (Entry<?, ?> entry : map.entrySet()) {

                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    elems.add(String.format("%s:%s", debug(key, depth + 1), debug(val, depth + 1)));
                }

                return String.format("[%s:%d=%s]",
                    argClassName,
                    map.size(),
                    String.join(",", elems));
            }

        } else if (isPrimitive(arg)) {

            return String.valueOf(arg);

        } else if (arg instanceof String) {

            // We already know it's not null, so just wrap it.
            return String.format("\"%s\"", arg);

        } else {

            // Probably a custom class.
            return arg.toString();
        }
    }

    /**
     * Returns true if the arg is a primitive type.
     */
    private static boolean isPrimitive(Object arg) {

        if (arg == null) {
            return false;
        }

        if (arg.getClass().isPrimitive()) {
            return true;
        }

        return
            arg instanceof Boolean ||
                arg instanceof Character ||
                arg instanceof Byte ||
                arg instanceof Short ||
                arg instanceof Integer ||
                arg instanceof Long ||
                arg instanceof Float ||
                arg instanceof Double;
    }

    public static boolean isTrue(Object obj) {

        if (obj == null) {

            return false;

        } else if (obj instanceof Boolean) {

            return (Boolean) obj;

        } else {
            return !isInvalid(obj);
        }
    }

    public static boolean isFalse(Object obj) {
        return !isTrue(obj);
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

    public static String throwIfEmpty(String val) {
        return throwIfEmpty(val, "Empty value.");
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

    public static <T extends Map<?, ?>> T throwIfEmpty(T val) {
        return throwIfEmpty(val, "Empty value.");
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

    public static <T extends Collection<?>> T throwIfEmpty(T val) {
        return throwIfEmpty(val, "Empty value.");
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

    public static <T> T[] throwIfEmpty(T[] val) {
        return throwIfEmpty(val, "Empty value.");
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
    // NULL SAFE:

    public static String nullSafe(String val) {
        return orIfNull(val, emptyString());
    }

    public static Integer nullSafe(Integer val) {
        return orIfNull(val, emptyInt());
    }

    public static Long nullSafe(Long val) {
        return orIfNull(val, emptyLong());
    }

    public static Float nullSafe(Float val) {
        return orIfNull(val, emptyFloat());
    }

    public static Double nullSafe(Double val) {
        return orIfNull(val, emptyDouble());
    }

    public static Boolean nullSafe(Boolean val) {
        return orIfNull(val, emptyBoolean());
    }

    public static <T extends Collection<?>> T nullSafe(T val) {
        return orIfNull(val, emptyCollection());
    }

    public static <T extends List<?>> T nullSafe(T val) {
        return orIfNull(val, emptyList());
    }

    public static <T extends Set<?>> T nullSafe(T val) {
        return orIfNull(val, emptySet());
    }

    public static <T extends Map<?, ?>> T nullSafe(T val) {
        return orIfNull(val, emptyMap());
    }

    public static <T> T[] nullSafe(T[] val) {
        return orIfNull(val, emptyArray());
    }

    public static File nullSafe(File val) {
        return orIfNull(val, emptyFile());
    }

    public static Path nullSafe(Path val) {
        return orIfNull(val, emptyPath());
    }

    /*

        public Set<Repository> findOrgRepo(String orgName, String repoNamePartial) {

        List<Repository> orgRepos = repoSvc().getOrgRepositories(orgName);

        if (orgRepos == null || orgRepos.isEmpty()) {
            return null;
        }

        return orgRepos.stream()
            .filter(r -> r.getName().contains(repoNamePartial))
            .collect(Collectors.toSet());

    }

    public Set<Repository> findOrgRepo(String orgName, String repoNamePartial) {

        return Val.nullSafe(repoSvc().getOrgRepositories(orgName))
            .stream()
            .filter(r -> r.getName().contains(repoNamePartial))
            .collect(Collectors.toSet());
    }


     */

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
    // EQUALITY:

    public static boolean equals(Object a, Object b) {

        // Technically, you're equal because you're both null.
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        // Pointer equality.
        if (a == b) {
            return true;
        }

        // Finally use their own equals method.
        return a.equals(b);
    }

    public static boolean notEquals(Object a, Object b) {
        return !equals(a, b);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // STRINGS:

    public static boolean equals(String a, String b) {

        // Technically, you're equal because you're both null.
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.equals(b);
    }

    public static boolean equalsIgnoreCase(String a, String b) {

        // Technically, you're equal because you're both null.
        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.equalsIgnoreCase(b);
    }

    public static boolean notEquals(String a, String b) {
        return !equals(a, b);
    }

    public static boolean notEqualsIgnoreCase(String a, String b) {
        return !equalsIgnoreCase(a, b);
    }

    public static boolean equalsAny(String str, String... tests) {

        // Technically, you're equal because you're both null.
        if (str == null && (tests == null || tests.length == 0)) {
            return true;
        }

        if (str == null || (tests == null || tests.length == 0)) {
            return false;
        }

        for (String test : tests) {
            if (equals(str, test)) {
                return true;
            }
        }

        return false;
    }

    public static boolean equalsAnyIgnoreCase(String str, String... tests) {

        // Technically, you're equal because you're both null.
        if (str == null && (tests == null || tests.length == 0)) {
            return true;
        }

        if (str == null || (tests == null || tests.length == 0)) {
            return false;
        }

        for (String test : tests) {
            if (equalsIgnoreCase(str, test)) {
                return true;
            }
        }

        return false;
    }

    public static boolean notEqualsAny(String str, String... tests) {
        return !equalsAny(str, tests);
    }

    public static boolean notEqualsAnyIgnoreCase(String str, String... tests) {
        return !equalsAnyIgnoreCase(str, tests);
    }

    /**
     * Returns true if [text] contains [findStr].
     * If either arg is null, returns false.
     * Better than {@link String#contains(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean contains(String text, String findStr) {

        // Null contains nothing.
        if (text == null) {
            return false;
        }

        // Something contains nothing I guess.
        if (findStr == null) {
            return true;
        }

        return text.contains(findStr);
    }

    public static boolean notContains(String text, String findStr) {
        return !contains(text, findStr);
    }

    /**
     * Returns true if [text] contains [findStr]. Ignores case.
     * If either arg is null, returns false.
     * Better than {@link String#contains(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean containsIgnoreCase(String text, String findStr) {

        // Null contains nothing.
        if (text == null || findStr == null) {
            return false;
        }

        return text.toLowerCase().contains(findStr.toLowerCase());
    }

    public static boolean notContainsIgnoreCase(String text, String findStr) {
        return !containsIgnoreCase(text, findStr);
    }

    /**
     * Returns true if [text] contains any of the [findStrs].
     * If either arg is null, returns false.
     * Better than {@link String#contains(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean containsAny(String text, Collection<String> findStrs) {

        // Null contains nothing.
        if (text == null || findStrs == null || findStrs.isEmpty()) {
            return false;
        }

        for (String findStr : findStrs) {
            if (contains(text, findStr)) {
                return true;
            }
        }

        return false;
    }

    public static boolean containsAny(String text, String... findStrs) {
        return containsAny(text, Arrays.asList(findStrs));
    }

    public static boolean notContainsAny(String text, Collection<String> findStrs) {
        return !containsAny(text, findStrs);
    }

    public static boolean notContainsAny(String text, String... findStrs) {
        return notContainsAny(text, Arrays.asList(findStrs));
    }

    /**
     * Returns true if [text] contains any of the [findStrs]. Ignores case.
     * If either arg is null, returns false.
     * Better than {@link String#contains(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean containsAnyIgnoreCase(String text, String... findStrs) {

        // Null contains nothing.
        if (text == null || findStrs == null || findStrs.length == 0) {
            return false;
        }

        String lowerText = text.toLowerCase();

        for (String findStr : findStrs) {

            if (findStr == null) {
                continue;
            }

            if (contains(lowerText, findStr.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static boolean notContainsAnyIgnoreCase(String text, String... findStrs) {
        return !containsAnyIgnoreCase(text, findStrs);
    }

    /**
     * Returns true if [text] ends with [findStr].
     * If either arg is null, returns false.
     * Better than {@link String#endsWith(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean endsWith(String text, String findStr) {

        // Null ends with nothing.
        if (text == null || findStr == null) {
            return false;
        }

        return text.endsWith(findStr);
    }

    public static boolean notEndsWith(String text, String findStr) {
        return !endsWith(text, findStr);
    }

    /**
     * Returns true if [text] ends with [findStr]. Ignores case.
     * If either arg is null, returns false.
     * Better than {@link String#endsWith(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean endsWithIgnoreCase(String text, String findStr) {

        // Null ends with nothing.
        if (text == null || findStr == null) {
            return false;
        }

        return text.toLowerCase().endsWith(findStr.toLowerCase());
    }

    public static boolean notEndsWithIgnoreCase(String text, String findStr) {
        return !endsWithIgnoreCase(text, findStr);
    }

    /**
     * Returns true if [text] ends with any of the [findStrs].
     * If either arg is null, returns false.
     * Better than {@link String#endsWith(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean endsWithAny(String text, Collection<String> findStrs) {

        // Null ends with nothing.
        if (text == null || findStrs == null || findStrs.isEmpty()) {
            return false;
        }

        for (String findStr : findStrs) {
            if (endsWith(text, findStr)) {
                return true;
            }
        }

        return false;
    }

    public static boolean endsWithAny(String text, String... findStrs) {
        return endsWithAny(text, Arrays.asList(findStrs));
    }

    public static boolean notEndsWithAny(String text, Collection<String> findStrs) {
        return !endsWithAny(text, findStrs);
    }

    public static boolean notEndsWithAny(String text, String... findStrs) {
        return notEndsWithAny(text, Arrays.asList(findStrs));
    }

    /**
     * Returns true if [text] ends with any of the [findStrs]. Ignores case.
     * If either arg is null, returns false.
     * Better than {@link String#endsWith(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean endsWithAnyIgnoreCase(String text, String... findStrs) {

        // Null ends with nothing.
        if (text == null || findStrs == null || findStrs.length == 0) {
            return false;
        }

        String lowerText = text.toLowerCase();

        for (String findStr : findStrs) {

            if (findStr == null) {
                continue;
            }

            if (endsWith(lowerText, findStr.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    public static boolean notEndsWithAnyIgnoreCase(String text, String... findStrs) {
        return !endsWithAnyIgnoreCase(text, findStrs);
    }

    /**
     * Returns true if [text] contains all of the [findStrs].
     * If either arg is null, returns false.
     * Better than {@link String#contains(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean containsAll(String text, String... findStrs) {

        // Null contains nothing.
        if (text == null || findStrs == null || findStrs.length == 0) {
            return false;
        }

        for (String findStr : findStrs) {
            if (!contains(text, findStr)) {
                return false;
            }
        }

        return true;
    }

    public static boolean notContainsAll(String text, String... findStrs) {
        return !containsAll(text, findStrs);
    }

    /**
     * Returns true if [text] contains all of the [findStrs]. Ignores case.
     * If either arg is null, returns false.
     * Better than {@link String#contains(CharSequence)} in that this version handles nulls for either arg.
     */
    public static boolean containsAllIgnoreCase(String text, String... findStrs) {

        // Null contains nothing.
        if (text == null || findStrs == null || findStrs.length == 0) {
            return false;
        }

        String lowerText = text.toLowerCase();

        for (String findStr : findStrs) {

            if (findStr == null) {
                continue;
            }

            if (!contains(lowerText, findStr.toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    public static boolean notContainsAllIgnoreCase(String text, String... findStrs) {
        return !containsAllIgnoreCase(text, findStrs);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Contains words - regex.

    public static boolean containsAllRegex(String text, Set<Pattern> findPats) {

        // Null contains nothing.
        if (anyInvalid(text, findPats)) {
            return false;
        }

        for (Pattern findPat : findPats) {

            boolean foundMatch = findPat.matcher(text).find();

            if (!foundMatch) {
                return false;
            }
        }

        return true;
    }

    public static boolean notContainsAllRegex(String text, Set<Pattern> findPats) {
        return !containsAllRegex(text, findPats);
    }

    public static boolean containsAnyRegex(String text, Set<Pattern> findPats) {

        // Null contains nothing.
        if (anyInvalid(text, findPats)) {
            return false;
        }

        for (Pattern findPat : findPats) {

            boolean foundMatch = findPat.matcher(text).find();

            if (foundMatch) {
                return true;
            }
        }

        return false;
    }

    public static boolean notContainsAnyRegex(String text, Set<Pattern> findPats) {
        return !containsAnyRegex(text, findPats);
    }

    public static boolean containsRegex(String text, Pattern findPat) {

        // Null contains nothing.
        if (anyInvalid(text, findPat)) {
            return false;
        }

        return findPat.matcher(text).find();
    }

    public static boolean notContainsRegex(String text, Pattern findPat) {
        return !containsRegex(text, findPat);
    }

    /**
     * You want to find out if contains(text, "cat")
     * but you don't care if contains(text, "catsup").
     * What you really want, is containsRegex(text, "\\bcat\\b").
     *
     * Well now you can: containsRegex(text, boundWords("cat")).
     * You can also make this nice and performant by reusing.
     * @param words strings of words to turn bind with regex - special regex chars will be escaped, don't worry
     * @return set of regex patterns with your words, bounded
     */
    private static Set<Pattern> bindRegexWords(boolean ignoreCase, String... words) {

        if (isEmpty(words)) {
            return emptySet();
        }

        int flags = ignoreCase ? Pattern.CASE_INSENSITIVE : 0;

        return Arrays.stream(words)                                      // For each word
            .map(Val::escapeRegex)                                       // Escape special chars
            .map(r -> Pattern.compile("\\b" + r + "\\b", flags))   // Then add word boundaries
            .collect(Collectors.toSet());                                // And collect as a set
    }

    /**
     * You want to find out if contains(text, "cat")
     * but you don't care if contains(text, "catsup").
     * What you really want, is containsRegex(text, "\\bcat\\b").
     *
     * Well now you can: containsRegex(text, boundWords("cat")).
     * You can also make this nice and performant by reusing.
     * @param words strings of words to turn bind with regex - special regex chars will be escaped, don't worry
     * @return set of regex patterns with your words, bounded
     */
    public static Set<Pattern> boundWords(String... words) {
        return bindRegexWords(false, words);
    }

    /**
     * You want to find out if contains(text, "cat")
     * but you don't care if contains(text, "catsup").
     * What you really want, is containsRegex(text, "\\bcat\\b").
     *
     * Well now you can: containsRegex(text, boundWords("cat")).
     * You can also make this nice and performant by reusing.
     * @param words strings of words to turn bind with regex - special regex chars will be escaped, don't worry
     * @return set of regex patterns with your words, bounded
     */
    public static Set<Pattern> boundWordsIgnoreCase(String... words) {
        return bindRegexWords(true, words);
    }

    /**
     * You want to find out if contains(text, "cat")
     * but you don't care if contains(text, "catsup").
     * What you really want, is containsRegex(text, "\\bcat\\b").
     *
     * Well now you can: containsRegex(text, boundWords("cat")).
     * You can also make this nice and performant by reusing.
     * @param words strings of words to turn bind with regex - special regex chars will be escaped, don't worry
     * @return set of regex patterns with your words, bounded
     */
    public static Pattern boundWord(String word) {

        for (Pattern wordPat : bindRegexWords(false, word)) {
            return wordPat;
        }

        return emptySet();
    }

    /**
     * You want to find out if contains(text, "cat")
     * but you don't care if contains(text, "catsup").
     * What you really want, is containsRegex(text, "\\bcat\\b").
     *
     * Well now you can: containsRegex(text, boundWords("cat")).
     * You can also make this nice and performant by reusing.
     * @param words strings of words to turn bind with regex - special regex chars will be escaped, don't worry
     * @return set of regex patterns with your words, bounded
     */
    public static Pattern boundWordIgnoreCase(String word) {

        for (Pattern wordPat : bindRegexWords(true, word)) {
            return wordPat;
        }

        return emptySet();
    }

    private static final Set<String> regexEscapeChars = new SetBuilder<String>()
        .addAll("[", "]", "(", ")", "{", "}", "\\", "^", "$", ".", "|", "?", "*", "+")
        .toHashSet();

    private static String escapeRegex(String regex) {

        if (isEmpty(regex)) {
            return regex;
        }

        StringBuilder bui = new StringBuilder();

        for (int i = 0; i < regex.length(); i++) {

            String sub = regex.substring(i, i + 1);

            if (regexEscapeChars.contains(sub)) {
                bui.append('\\');
            }

            bui.append(sub);
        }

        return bui.toString();
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

        if (Val.isEmpty(nnullSafe(urlVerificationReq).challenge)) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        if (urlVerificationReq == null || urlVerificationReq.challenge == null || urlVerificationReq.challenge.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).build();
        }

 */
