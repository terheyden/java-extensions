package com.terheyden.jext;

import com.terheyden.jext.RegexBuilder;
import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class RegexBuilderTest {

    @Test
    public void regular() {

        String normal1 = "a \\w+ normal reg[a-z] expression";

        assertEquals(normal1,
        RegexBuilder
            .regex(normal1)
            .buildString());
    }

    @Test
    public void simpleSpaces() {

        Pattern pat01 = new RegexBuilder("this has easy spaces")
            .simpleSpaces()
            .buildPattern();

        assertTrue(pat01.matcher("this\t has   easy\t\t spaces").find());

        assertFalse(pat01.matcher("thishaseasyspaces").find());

        Pattern pat02 = new RegexBuilder(" this  has\t\tmore spaces   ")
            .simpleSpaces()
            .buildPattern();

        assertTrue(pat02.matcher("this has more spaces").find());

        assertTrue(pat02.matcher("\t\tthis \t has more spaces\t\t\t").find());

        assertFalse(pat02.matcher("thishasmorespaces").find());

        Pattern pat03 = new RegexBuilder(" spaces == optional ")
            .simpleSpaces()
            .buildPattern();

        assertTrue(pat03.matcher("  spaces   ==   optional   ").find());
        assertTrue(pat03.matcher("spaces==optional").find());
    }

    @Test
    public void smartOrs() {

        Pattern pat01 = new RegexBuilder("this|that thing")
            .simpleOrs()
            .buildPattern();

        Matcher mat01 = pat01.matcher("this thing");
        assertTrue(mat01.find() && "this thing".equals(mat01.group()));
        Matcher mat02 = pat01.matcher("that thing");
        assertTrue(mat02.find() && "that thing".equals(mat02.group()));

        Pattern pat03 = new RegexBuilder("I have|petted|pet my|your pet dog|cat")
            .simpleOrs()
            .buildPattern();

        Matcher mat03 = pat03.matcher("I have your pet cat");
        Matcher mat04 = pat03.matcher("I petted my pet dog");
        Matcher mat05 = pat03.matcher("I pet your pet dog");

        assertTrue(mat03.find() && "I have your pet cat".equals(mat03.group()));
        assertTrue(mat04.find() && "I petted my pet dog".equals(mat04.group()));
        assertTrue(mat05.find() && "I pet your pet dog".equals(mat05.group()));

        assertEquals("(?:my|your).+(?:things|stuff)", RegexBuilder
            .regex("my|your.+things|stuff")
            .simpleOrs()
            .buildString()
        );
    }

    @Test
    public void easyVars() {

        Pattern pat01 = new RegexBuilder("--login %login --email {my@email.com}")
            .var("<word>", "[A-Za-z0-9_.-]+")
            .var("{email}", "<word>@<word>")
            .var("%login", "<word>")
            .var("{my@email.com}", "{email}")
            .buildPattern();

        assertTrue(pat01.matcher("--login lterheyden --email luke.terheyden@gmail.com").find());
    }

//    @Test
//    public void autoCapture() {
//
//        Matcher mat01 = RegexBuilder
//            .regex("My name is _name_ and I am _age_.")
//            .autoCaptureVars()
//            .var("(word)", "[A-Za-z0-9._-]+")
//            .var("_name_", "(word)")
//            .var("_age_", "[1-9][0-9]*")
//            .buildMatcher("My name is luke and I am 29.");
//
//        assertTrue(mat01.find());
//        assertEquals("luke", mat01.group(1));
//        assertEquals("29", mat01.group(2));
//        assertEquals(mat01.groupCount(), 2);
//
//        Matcher mat02 = RegexBuilder
//            .regex("(<name>)")
//            .var("<name>", "[A-Za-z]+")
//            .buildMatcher("luke");
//
//        assertTrue(mat02.find());
//        assertEquals(mat02.groupCount(), 1);
//        assertEquals("luke", mat02.group(1));
//
//        Matcher mat03 = RegexBuilder
//            .regex("<name>")
//            .autoCaptureVars()
//            .var("<name>", "[A-Za-z]+")
//            .buildMatcher("luke");
//
//        assertTrue(mat03.find());
//        assertEquals(mat03.groupCount(), 1);
//        assertEquals("luke", mat03.group(1));
//    }

    @Test
    public void namedGroups() {

        Matcher mat01 = RegexBuilder
            .regex("My name is {name} and I am {age}.")
            .var("{word}", "[A-Za-z0-9._-]+")
            .var("{name}", "(?<name>{word})")
            .var("{age}", "(?<age>[1-9][0-9]*)")
            .buildMatcher("My name is luke and I am 29.");

        assertTrue(mat01.find());
        assertEquals("luke", mat01.group("name"));
        assertEquals("29", mat01.group("age"));
        assertEquals(mat01.groupCount(), 2);

        Matcher mat02 = RegexBuilder
            .regex("My name is {name} and I am {age}.")
            .var("{word}", "[A-Za-z0-9._-]+")
            .var("{name}", "{word}", "name")
            .var("{age}", "[1-9][0-9]*", "age")
            .buildMatcher("My name is luke and I am 29.");

        assertTrue(mat02.find());
        assertEquals("luke", mat02.group("name"));
        assertEquals("29", mat02.group("age"));
        assertEquals(mat02.groupCount(), 2);

    }

    @Test
    public void explicitParens() {

        // Be smart and don't insert parens where they're not needed.

        assertEquals(
            "(we|you) should meet (yesterday|tomorrow)",
            RegexBuilder.regex("(we|you) should meet (yesterday|tomorrow)")
                .simpleOrs()
                .buildString()
            );

        assertEquals(
            "(we|you) should meet (?:yesterday|tomorrow)",
            RegexBuilder.regex("(we|you) should meet yesterday|tomorrow")
                .simpleOrs()
                .buildString()
            );
    }

    @Test
    public void isSubstringBeforeIndex() {

        assertFalse(RegexBuilder.isSubstringBeforeIndex("orange", 0, ""));
        assertTrue(RegexBuilder.isSubstringBeforeIndex("orange", 1, "o"));
        assertTrue(RegexBuilder.isSubstringBeforeIndex("orange", 2, "or"));

        assertFalse(RegexBuilder.isSubstringBeforeIndex("orange", 2, "o"));
        assertFalse(RegexBuilder.isSubstringBeforeIndex("orange", 1, "or"));
    }

    @Test
    public void smartParens() {

        String str1a = "My 'name' is not \"hey buddy.\"";
        String str1b = "My 'name' is not \"hey buddy.\"";

        String str2 = "\"name\" : \"{login}\"";

        assertEquals(str1a, RegexBuilder
            .regex(str1b)
            .buildString());
    }
}
