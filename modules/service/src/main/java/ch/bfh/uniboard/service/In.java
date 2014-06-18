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

import java.util.List;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class In extends Constraint {

    private List<Value> element;

    public In(List<Value> element, List<String> keys, PostElement postElement) {
        super(keys, postElement);
        this.element = element;
    }

    public List<Value> getSet() {
        return element;
    }

    public void setSet(List<Value> set) {
        this.element = set;
    }
}
