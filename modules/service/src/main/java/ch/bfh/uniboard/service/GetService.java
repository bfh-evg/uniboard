/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
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

import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.ResultContainer;
import javax.ejb.Local;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Local
public interface GetService {

	/**
	 * Queries the bulletin board asking a component implementing this interface.
	 *
	 * @param query a query containing all required information elements
	 * @return a result
	 */
	public ResultContainer get(Query query);
}
