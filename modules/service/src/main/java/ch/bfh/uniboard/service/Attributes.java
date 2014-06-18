/*
 * Copyright (c) 2014 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.service;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A data container for arbitrary key/value pairs. Keys are strings, values can
 * be anything. Values should be immutable. Map entries are ordered by
 * insertion.
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Attributes implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String ERROR = "error";
    public static final String REJECTED = "rejected";

    /**
     * Holder of key/value pairs.
     */
    private final LinkedHashMap<String, Value> map;

    public Attributes() {
        map = new LinkedHashMap<>();
    }

    /**
     * Initializes the data container
     *
     * @param map a map initialized with key/value pairs; values should be
     * immutable
     */
    public Attributes(LinkedHashMap<String, Value> map) {
        this.map = map;
    }

    /**
     * Returns the value associated with given key, or null, if key is not in
     * the map.
     *
     * @param key a key
     * @return associated value, or null
     */
    public Value getValue(String key) {
        return this.map.get(key);
    }

    /**
     * Returns all keys in the map.
     *
     * @return a set of keys
     */
    public Set<String> getKeys() {
        return this.map.keySet();
    }

    /**
     *
     * @return the set of entries
     */
    public Set<Map.Entry<String, Value>> getEntries() {
        return this.map.entrySet();
    }

    /**
     * Adds an attribute at the end of the map
     *
     * @param key a key
     * @param value associated value, or null
     */
    public void add(String key, Value value) {
        this.map.put(key, value);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.map);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attributes other = (Attributes) obj;
        if (!Objects.equals(this.map, other.map)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Attributes{" + "map=" + map + '}';
    }

}
