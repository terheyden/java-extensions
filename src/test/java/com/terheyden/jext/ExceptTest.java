package com.terheyden.jext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class ExceptTest {

    public URL throwUp() throws MalformedURLException {
        return new URL("bad uri");
    }

    @Test
    public void testIgnore() {

        // Ignore simply ignores exceptions.
        URL url1 = Except.ignore(() -> throwUp());
        assertNull(url1);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWrap() {

        expectedException.expect(MalformedURLException.class);

        // Wrap does a sneaky throw.
        URL url2 = Except.wrap(() -> throwUp());
    }

}