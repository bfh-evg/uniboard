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

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public abstract class GetComponent implements GetService {

	@Override
	public final ResultContainer get(Query query) {
		//do some verification actions
		beforeGet(query);
		//pass content received to the successor
		ResultContainer resultContainer = this.getGetSuccessor().get(query);
		//do some actions with content returned by successor
		Attributes newGamma = this.afterGet(query, resultContainer);
		//put the attributes processed in the previously received resultContainer
		resultContainer.setGamma(newGamma);
		//return the result container
		return resultContainer;
	}

	/**
	 * Actions done on the query before passing it to the successor. This method can only do verifications but cannot
	 * add information
	 *
	 * @param query the query to fulfill
	 */
	protected void beforeGet(Query query) {
		// default implementation
	}

	/**
	 * Actions done after receiving the result from the successor. Changes can only be done in the attributes
	 *
	 * @param query the query to fulfill
	 * @param resultContainer the container with the result to the query and the attributes
	 * @return the attributes processed
	 */
	protected Attributes afterGet(Query query, ResultContainer resultContainer) {
		// default implementation
		return resultContainer.getGamma();
	}

	/**
	 * Returns the get successor layer in the layer stack
	 *
	 * @return the get service coming after this layer
	 */
	protected abstract GetService getGetSuccessor();
}
