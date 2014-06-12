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
import java.util.List;

/**
 * A data container for arbitrary key/value pairs. Keys are strings, values can be anything. Values should be immutable.
 * Map entries are sorted by key values.
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
public class Query implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Constraint> constraints;

	/**
	 * Initializes the data container
	 *
	 * @param constraints
	 */
	public Query(List<Constraint> constraints) {
		this.constraints = constraints;
	}

    public List<Constraint> getConstraints() {
        return constraints;
    }
        
        

}
