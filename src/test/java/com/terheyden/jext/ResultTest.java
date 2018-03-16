package com.terheyden.jext;

import org.junit.Test;

import static org.junit.Assert.*;

public class ResultTest {

    @Test
    public void test() {

        // Let's say you do some shit and it works perfectly.

        TestUser user01 = new TestUser("mika", 12);

        Result<TestUser> good01 = Result.ok(user01);

        // Caller can extract the results.

        TestUser user01a = good01.getValue();

        TestUser user01b = good01.getValueOrDefault(user01);

        assertEquals(user01, user01a);
        assertEquals(user01, user01b);

        // You can also return fails.

        Result<TestUser> fail01 = Result.fail("It totally failed.");

        // If you want, you can stream the result, like with Optional<> but less confusing.

        good01.resultStream().forEach(u -> System.out.println(u.name()));
        fail01.resultStream().forEach(u -> System.out.println(u.name()));
    }
}
