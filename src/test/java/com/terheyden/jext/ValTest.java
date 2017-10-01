package com.terheyden.jext;

import com.terheyden.jext.Val;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
    @Ignore
    public void testGuard() {

        String name = "Cora";
        int age = 6;

        Val.guard(Val.isEmpty(name), "Name is invalid")
            .guard(age < 18, "You are too young for this test.");

    }

}