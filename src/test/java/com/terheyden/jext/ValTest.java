package com.terheyden.jext;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class ValTest {

    private final String nullStr = null;
    private final String[] emptyStrs = { null, "", "\n", "\t\n\r" };
    private final String[] emptyStrArray = new String[0];
    private final Map<String, String> emptyMap = new HashMap<>();

    @Test
    public void empty() {

        assertTrue(Val.isEmpty(nullStr));
        assertTrue(Val.isEmpty(""));
        assertTrue(Val.isEmpty(" \t \r\n "));
        assertFalse(Val.isEmpty("."));
        assertTrue(Val.isEmpty(emptyStrArray));
        assertFalse(Val.isEmpty(emptyStrs));
        assertTrue(Val.isEmpty(emptyMap));
    }

    @Test
    public void bulkEmpty() {

        String nullStr = null;
        assertTrue(Val.allEmpty("", "\n", null));
        assertFalse(Val.allEmpty(" ", "!", null));
        assertTrue(Val.anyEmpty("one", "two", ""));
        assertTrue(Val.anyEmpty("", "hi"));
        assertFalse(Val.anyEmpty("foo", "hi"));

        assertTrue(Val.allNotEmpty("a", "b"));
        assertFalse(Val.allNotEmpty("a", "b", ""));

        assertTrue(Val.anyNotEmpty("", "a", ""));
        assertFalse(Val.anyNotEmpty(""));
    }

    @Test
    public void testContains() {

        // Nulls are false.
        assertFalse(Val.contains(null, null));
        assertFalse(Val.contains(null, "yes"));
        assertFalse(Val.contains("yes", null));

        // Simple cases.
        assertTrue(Val.contains("hello", "hello"));
        assertTrue(Val.contains("hello", "hell"));
        assertTrue(Val.contains("hello", "ello"));
        assertFalse(Val.contains("hello", "hellow"));
        assertFalse(Val.contains("hello", "yello"));
        assertFalse(Val.contains("hello", "HELL"));

        assertTrue(Val.containsIgnoreCase("hello", "HELL"));
        assertTrue(Val.containsAnyIgnoreCase("hello", "HELL"));
        assertTrue(Val.containsAllIgnoreCase("hello", "HELL"));

        assertTrue(Val.containsAnyIgnoreCase("hello", "HELL", "MOO"));
        assertFalse(Val.containsAllIgnoreCase("hello", "HELL", "MOO"));
    }

    private final String goodStr = "good";
    private final Path goodPath = Paths.get(System.getProperty("user.home"));
    private final File goodFile = new File(System.getProperty("user.dir"));
    private final Map<String, Path> goodMap = Collections.singletonMap("good", goodPath);
    private final List<File> goodList = Collections.singletonList(goodFile);
    private final Object goodObj = new SimpleEntry<>(0F, 0D);
    private final Path badPath = Paths.get("/tmp/fake_file.xxx");
    private final Map<String, Path> badMap = new HashMap<>();
    private final List<String> badList = new LinkedList<>();
    private final Object badObj = null;

    @Test
    public void testValidateGood1() {

        // Should validate all our good stuff.
        Val.throwIfAnyInvalid(
            goodStr,
            goodPath,
            goodFile,
            goodMap,
            goodList,
            goodObj
        );
    }

    @Rule
    public final ExpectedException ex = ExpectedException.none();

    @Test
    public void testValidateBad1() {
        ex.expect(IllegalArgumentException.class);
        Val.throwIfAnyInvalid(badList);
    }

    @Test
    public void testValidateBad2() {
        ex.expect(IllegalArgumentException.class);
        Val.throwIfAnyInvalid(
            goodStr,
            badList);
    }

    @Test
    public void testValidateBad3() {
        ex.expect(IllegalArgumentException.class);
        Val.throwIfAnyInvalid(
            goodMap,
            badList,
            goodStr
        );
    }

    @Test
    @Ignore
    public void testGuard() {

        String name = "Cora";
        int age = 6;

        Val.guard(Val.isEmpty(name), "Name is invalid")
            .guard(age < 18, "You are too young for this test.");

    }

    @Test
    public void testContainsRegex() {

        Set<Pattern> pats = new SetBuilder<Pattern>()
            .add(Pattern.compile("\\bcat\\b", Pattern.CASE_INSENSITIVE))
            .add(Pattern.compile("\\bdog\\b", Pattern.CASE_INSENSITIVE))
            .toHashSet();

        // Test that word boundaries work.
        assertFalse(Val.containsAllRegex("catch and dogs", pats));
        assertTrue(Val.containsAllRegex("cat and dog", pats));

        // All vs. any.
        assertTrue(Val.containsAnyRegex("dog", pats));
        assertFalse(Val.containsAllRegex("cat and dogs", pats));
    }

    @Test
    public void testRegexWords() {

        // Word boundaries, \b, only work on alpha-nums.
        // So our special must start and end with alpha-nums.
        String special = "hey].?\\now*{3";
        Set<Pattern> wordsC = Val.boundWords("cat", special);

        // Verify word boundaries:
        assertTrue(Val.containsAnyRegex("that's my cat", wordsC));
        assertFalse(Val.containsAnyRegex("catcher in the rye", wordsC));

        // Verify case:
        assertTrue(Val.containsAnyRegex("cat", wordsC));
        assertFalse(Val.containsAnyRegex("CAT", wordsC));

        Pattern catIC = Val.boundWordIgnoreCase("cat");
        assertTrue(Val.containsRegex("cat", catIC));
        assertTrue(Val.containsRegex("CAT", catIC));

        // Should ignore all specials.
        assertTrue(Val.containsAnyRegex(special, wordsC));
    }

    @Test
    public void testDebug() {

        Set<Object> set = new SetBuilder<Object>()
            .add(new ListBuilder<Object>().add("list1").add("list2").toArrayList())
            .add(new ForkJoinPool())
            .add(3)
            .add(true)
            .toHashSet();

        System.out.println(Val.toDebugStr(set));
    }

}
