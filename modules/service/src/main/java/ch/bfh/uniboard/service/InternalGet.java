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
 * Generic interface for a component providing the persistence. Components that require information about posts on the
 * board to reach a decision should use this interface for data retrieval, as it reduces the load by not providing any
 * properties.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public interface InternalGet {

	/**
	 * Queries the bulletin board asking a component implementing this interface.
	 *
	 * @param q a query containing all required information elements
	 * @return the resulting post to the query
	 */
	public List<Post> internalGet(Query q);

}
