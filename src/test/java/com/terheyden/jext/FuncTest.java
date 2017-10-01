package com.terheyden.jext;

import com.terheyden.jext.Func;
import org.junit.Ignore;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class FuncTest {

    public URL throwUp() throws MalformedURLException {
        return new URL("bad uri");
    }

    @Test
    @Ignore
    public void test() {

        URL url01 = Func.supplier(this::throwUp).ignoreExceptions().get();

    }

}