package com.terheyden.jext;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayDeque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Build simpler, smarter regular expressions.
 *
 * - multiple construction styles:
 *   - new RegexBuilder("str")
 *   - RegexBuilder.regex("str")
 *
 * Smart features (optional):
 *
 *   Smart spaces:
 *
 *     "my string" == "my\s+string"
 *
 *     " name = Cora " == "\s*name\s*=\s*Cora\s*"
 *
 *   Smart ORs:
 *
 *     "enter your login|pwd" == "enter your (?:login|pwd)"
 *
 *   Transitive variables with auto-capture:
 *
 *     "{email}"
 *       .var("{word}", "[A-Za-z0-9._-]+")
 *       .var("{email}", "{word}@{word}")
 *
 *     == "([A-Za-z0-9._-]+@[A-Za-z0-9._-]+)"
 *
 * Check out the unit tests for more examples.
 */
public final class RegexBuilder {

    // [A-Za-z0-9_-]+\|[A-Za-z0-9_-]+(?:\|[A-Za-z0-9_-]+)*
    private static final String orRegex = "[A-Za-z0-9_-]+\\|[A-Za-z0-9_-]+(?:\\|[A-Za-z0-9_-]+)*";
    private static final Pattern orPat = Pattern.compile(orRegex);

    private String regex;
    private int flags;
    private boolean autoCaptureVars;

    // Vars are transitive, defined in order, so use a stack.
    // ArrayDeques are the fastest stack objs.
    private ArrayDeque<SimpleEntry<String, String>> _vars;

    ////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCT:

    public RegexBuilder(String regex) {
        this.regex = regex;
    }

    public static RegexBuilder regex(String regex) {
        return new RegexBuilder(regex);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // FLAGS:

    /**
     * Build a case-insensitive regex.
     */
    public RegexBuilder ignoreCase() {
        flags |= Pattern.CASE_INSENSITIVE;
        return this;
    }

    /**
     * Dots in the regex will buildMatcher newlines (DOTALL). By default, they do not.
     */
    public RegexBuilder dotsMatchNewlines() {
        flags |= Pattern.DOTALL;
        return this;
    }

    /**
     * The begin and end symbols, ^ and $, buildMatcher the beginning and end of the entire expression, by default.
     * Call this to have them buildMatcher the begin and end of each line instead (MULTILINE).
     */
    public RegexBuilder beginAndEndMatchNewlines() {
        flags |= Pattern.MULTILINE;
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // TRANSFORM:

//    /**
//     * Automatically put vars in capture groups.
//     */
//    public RegexBuilder autoCaptureVars() {
//        autoCaptureVars = true;
//        return this;
//    }

    // TODO: I turned this off. Special features should be explicit.
//    /**
//     * Turn on all the simplifying things - simple spaces, simple ORs, auto-capture vars.
//     */
//    public RegexBuilder smartMode() {
//        simpleOrs();
//        simpleSpaces();
////        autoCaptureVars();
//        return this;
//    }

    private static final Pattern spacePat = Pattern.compile("\\s+");

    /**
     * Spaces are really \s+, any continuous collection of space characters.
     */
    public RegexBuilder simpleSpaces() {

        // Find all spaces and see if we should use \s+ or \s* ...

        StringBuilder bui = new StringBuilder();
        Matcher spaceMat = spacePat.matcher(regex);
        int lastStart = 0;
        int start = 0;
        int end = 0;

        while (spaceMat.find()) {

            start = spaceMat.start();
            end = spaceMat.end();

            boolean isAlphaBefore = start > 0 && isAlphaNum(regex.charAt(start - 1));

            boolean isAlphaAfter = end < regex.length() && isAlphaNum(regex.charAt(end));

            // Append before the match...
            bui.append(regex, lastStart, start);

            // Replace the matching strings with \s:
            bui.append(isAlphaBefore && isAlphaAfter ? "\\s+" : "\\s*");

            lastStart = end;
        }

        // Add the last part...
        bui.append(regex, lastStart, regex.length());

        regex = bui.toString();
        return this;
    }

    private boolean isAlphaNum(char c) {
        return
            c >= '0' && c <= '9' ||
                c >= 'A' && c <= 'Z' ||
                c >= 'a' && c <= 'z';
    }

    /**
     * Use simplified ORs, e.g. "my cat|dog" == "my cat" | "my dog", not "my cat" | "dog".
     */
    public RegexBuilder simpleOrs() {

        String str = regex;
        Matcher orMat = orPat.matcher(str);
        StringBuilder bui = new StringBuilder();

        while (orMat.find()) {

            // Ignore if it's already surrounded by parens...
            int start = orMat.start();
            if (start > 0 && str.charAt(start - 1) == '(') {
                continue;
            }

            bui.append(str.substring(0, start));                    // Append anything before the buildMatcher.
            bui.append("(?:").append(orMat.group()).append(")");    // Wrap and append the buildMatcher...

            str = str.substring(orMat.end());                       // Keep looking at the rest of the str...
            orMat = orPat.matcher(str);
        }

        // Append the last piece.
        bui.append(str);

        regex = bui.toString();
        return this;
    }

    /**
     * Define a variable. Vars can contain any symbols. Vars are also transitive.
     * Ex: regbui.var("{word}", "[A-Za-z0-9._-]+").var("{email}", "{word}@{word}")...
     *
     * @param key       the var name
     * @param val       the var value, which must ultimately be a valid regex expression
     * @param groupName if specified, will surround the val in a named capture group
     */
    public RegexBuilder var(String key, String val, String groupName) {

        if (_vars == null) {
            _vars = new ArrayDeque<>();
        }

        // Create a regex group name, if desired.
        if (groupName != null && groupName.length() > 0) {
            val = String.format("(?<%s>%s)", groupName, val);
        }

        _vars.push(new SimpleEntry<>(key, val));
        return this;
    }

    public RegexBuilder var(String key, String val) {
        return var(key, val, null);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // FINISH BUILDING:

    /**
     * Called right as we finish up, applies all the vars the user added.
     */
    private void applyAllVars() {

        if (_vars == null || _vars.isEmpty()) {
            return;
        }

        while (!_vars.isEmpty()) {

            SimpleEntry<String, String> var = _vars.pop();
            String varKey = var.getKey();
            String varVal = var.getValue();

            applyVar(varKey, varVal);
        }
    }

    /**
     * Apply a single var to our regex.
     */
    private void applyVar(String find, String replace) {

        // We're not using replaceAll(), to avoid messing up special chars.
        String str = regex;
        StringBuilder bui = new StringBuilder();

        int off = str.indexOf(find);

        while (off > -1) {

            boolean surroundWithParens =                        // See if we should automatically
                autoCaptureVars &&                              // surround vars with parens.
                (off == 0 || str.charAt(off - 1) != '(');

            bui.append(str.substring(0, off));                  // Append anything before the match.
            bui.append(surroundWithParens ? "(" : "");          // Optionally surround with parens.
            bui.append(replace);                                // Append replace text.
            bui.append(surroundWithParens ? ")" : "");          // Close parens.

            str = str.substring(off + find.length());        // Move our test string past this last match.
            off = str.indexOf(find);                         // Set up for next loop.
        }

        // Add the last unmatched bit.
        bui.append(str);

        regex = bui.toString();
    }

    static boolean isSubstringBeforeIndex(String text, int index, String substring) {

        if (index <= 0) {
            return false;
        }

        if (substring == null || substring.isEmpty()) {
            return true;
        }

        if (text == null || text.length() < index) {
            return false;
        }

        int subLen = substring.length();

        if (index - subLen < 0) {
            return false;
        }

        return text.substring(index - subLen, index).equals(substring);
    }

    static boolean anySubstringBeforeIndex(String text, int index, String... substrings) {

        for (String sub : substrings) {
            if (isSubstringBeforeIndex(text, index, sub)) {
                return true;
            }
        }

        return false;
    }

    static int countRegexParens(String regex) {

        int count = 0;

        int off = regex.indexOf('(');

        while (off > -1) {

            // Ignore \(escaped\) parens:
            boolean isEscaped = off > 0 && regex.charAt(off - 1) == '\\';

            // Ignore (?:special) parens:
            boolean isSpecial = off < regex.length() - 1 && regex.charAt(off + 1) == '?';

            if (!isEscaped && !isSpecial) {
                count++;
            }

            off = regex.indexOf('(', off + 1);
        }

        return count;
    }

    /**
     * Apply finishing touches to our regex.
     */
    private void finish() {
        applyAllVars();
    }

    /**
     * Build our regex as a String and return it.
     */
    public String buildString() {
        finish();
        return regex;
    }

    // toString() is supposed to be idempotent (e.g. it's called by the debugger constantly).
    // our methods are not, so we're not overriding it.
//    @Override
//    public String toString() {
//        return buildString().toString();
//    }

    /**
     * Build the final regex into a Pattern and return it.
     */
    public Pattern buildPattern() {
        return Pattern.compile(buildString(), flags);
    }

    /**
     * Build the regex as a Matcher and return it.
     */
    public Matcher buildMatcher(String text) {
        return buildPattern().matcher(text);
    }

    /**
     * Return true if the final regex matches the specified text.
     */
    public boolean matches(String text) {
        return buildMatcher(text).find();
    }
}
