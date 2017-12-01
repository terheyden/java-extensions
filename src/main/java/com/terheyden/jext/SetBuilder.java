package com.terheyden.jext;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper to build a set.
 */
public class SetBuilder<T> {

    // Internally we'll use a HashSet for now.
    // We can add additional makers that convert.
    private final Set<T> items = new HashSet<>();

    public static <T> SetBuilder<T> newSet() {
        return new SetBuilder<>();
    }

    public SetBuilder<T> add(T item) {
        items.add(item);
        return this;
    }

    public SetBuilder<T> addAll(Collection<T> items) {
        this.items.addAll(items);
        return this;
    }

    public SetBuilder<T> addAll(T... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public Set<T> toHashSet() {
        return items;
    }

    /**
     * I have these versions because the built-in Java versions
     * will return immutable sets.
     */
    public static <T> HashSet<T> newHashSet(Collection<T> items) {

        return new HashSet<>(items);
    }

    /**
     * I have these versions because the built-in Java versions
     * will return immutable sets.
     */
    public static <T> HashSet<T> newHashSet(T... items) {

        HashSet<T> set = new HashSet<>();
        Collections.addAll(set, items);
        return set;
    }
}
