/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniVote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.service;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * A data container for arbitray key/value pairs. Keys are strings, values
 * can be anything. Values should be immutable.
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Holder of key/value pairs.
     */
    private Map<String, Object> map;

    /**
     * Initializes the data container
     * @param map a map initialized with key/value pairs; values should be immutable
     */
    public Message(Map<String, Object> map) {
        this.map = map;
    }

    /**
     * Returns the value associated with given key, or null, if
     * key is not in the map.
     *
     * @param key a key
     * @return assoiciated value, or null
     */
    public Object getValue(String key) {
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

    // TODO Add other methods if necessary
}
