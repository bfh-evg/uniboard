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
 * Abstract constraint allowing to restrict which result must be retrived in the persistence layer
 * A constraint is composed of a PostElement specifying to which part of the post the restriction applies,
 * one or more keys (in hierarchival order) indicating to which field inside the specified part of the post the restriction applies
 * and one or more values depending on the type of constraint.
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public abstract class Constraint {

    private List<String> keys;
    private PostElement postElement;

    /**
     * Create a constraint
     * @param keys the fields to which this restriction applies
     * @param postElement the part of the post to which the restiction applies
     */
    public Constraint(List<String> keys, PostElement postElement) {
        this.keys = keys;
        this.postElement = postElement;
    }
    
    /**
     * Get all keys (in hierarchival order) to which this constraint applies
     * @return keys (in hierarchival order)
     */
    public List<String> getKeys() {
        return keys;
    }

    /**
     * Get the post part to which this constraint applies
     * @return part of the post
     */
    public PostElement getPostElement() {
        return postElement;
    }

}
