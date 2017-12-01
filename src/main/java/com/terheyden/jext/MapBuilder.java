package com.terheyden.jext;

import java.util.HashMap;
import java.util.Map;

/**
 * Construct a {@link Map} with starting values and other things.
 */
public class MapBuilder<K, V> {

    // Internal tracking of the map entries.
    // Also our default / most used built type.
    private final HashMap<K, V> entries = new HashMap<>();

    public static <K, V> MapBuilder<K, V> newMap() {
        return new MapBuilder<>();
    }

    /**
     * Add an entry with this key and value to the map, when built.
     */
    public MapBuilder<K, V> put(K key, V val) {
        entries.put(key, val);
        return this;
    }

    /**
     * Build a {@link HashMap} from the builder settings.
     */
    public HashMap<K, V> buildHashMap() {

        // TODO: For other map types we can return e.g. new TreeMap<>(entries);
        return entries;
    }

    /**
     * I think they finally have an in-line map value adder in Java9.
     * But for now...
     */
    public static <K, V> Map<K, V> newHashMap(K key, V val) {

        Map<K, V> map = new HashMap<>();
        map.put(key, val);
        return map;
    }
}
