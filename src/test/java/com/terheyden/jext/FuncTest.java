package com.terheyden.jext;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class FuncTest {

    public URL throwUp() throws MalformedURLException {
        return new URL("bad uri");
    }

    @Test
    public void test() throws MalformedURLException {

        URL url01 = Func.quietly(this::throwUp).ignoreExceptions().get();
        assertNull(url01);

        Func.quietly(() -> { throw new RuntimeException("HI!"); });

        URL google = new URL("http://www.google.com");

        URL url02 = Func.quietly(() -> throwUp())
            .ignoreExceptions()
            .defaultValue(google)
            .get();

        assertEquals(google, url02);
    }
}
