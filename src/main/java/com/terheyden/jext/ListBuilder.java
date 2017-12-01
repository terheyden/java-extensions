package com.terheyden.jext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Helper to build a list.
 */
public class ListBuilder<T> {

    // Internally we'll use a linked list, because once we lose
    // order we can't get it back.
    private final LinkedList<T> items = new LinkedList<>();

    public static <T> ListBuilder<T> newList() {
        return new ListBuilder<>();
    }

    public ListBuilder<T> add(T item) {
        items.add(item);
        return this;
    }

    public ListBuilder<T> addAll(Collection<T> items) {
        this.items.addAll(items);
        return this;
    }

    public ListBuilder<T> addAll(T... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public ArrayList<T> toArrayList() {
        return new ArrayList<>(items);
    }

    public LinkedList<T> toLinkedList() {
        return items;
    }

    /**
     * Return a normal new {@link LinkedList} of items.
     */
    public static <T> List<T> newLinkedList(T... items) {

        List<T> list = new LinkedList<>();
        Collections.addAll(list, items);
        return list;
    }

    /**
     * Return a normal new {@link ArrayList} of items.
     */
    public static <T> List<T> newArrayList(T... items) {

        List<T> list = new ArrayList<>();
        Collections.addAll(list, items);
        return list;
    }
}
