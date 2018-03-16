package com.terheyden.jext;

import java.util.concurrent.atomic.AtomicLong;

/**
 * The canonical "user" class for testing stuff!
 */
public class TestUser {

    private static final AtomicLong idCounter = new AtomicLong(0);

    private final long id;
    private String name;
    private int age;

    public TestUser() {
        id = idCounter.incrementAndGet();
    }

    public TestUser(String name, int age) {
        this.id = idCounter.incrementAndGet();
        this.name = name;
        this.age = age;
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public TestUser name(String name) {
        this.name = name;
        return this;
    }

    public int age() {
        return age;
    }

    public TestUser age(int age) {
        this.age = age;
        return this;
    }
}
