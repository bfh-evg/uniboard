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

import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.ResultContainer;
import ch.bfh.uniboard.service.data.Attributes;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public abstract class Component implements PostService, GetService {

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		//do some preprocessing actions
		Attributes beforePost = this.beforePost(message, alpha, beta);
		//Check if the preprocessing created an error
		if (beforePost.getKeys().contains(Attributes.ERROR)) {
			Attributes errorBeta = new Attributes();
			errorBeta.add(beforePost.getAttribute(Attributes.ERROR));
			return errorBeta;
		} //Check if the preprocessing created an rejected the message
		else if (beforePost.getKeys().contains(Attributes.REJECTED)) {
			Attributes rejectBeta = new Attributes();
			rejectBeta.add(beforePost.getAttribute(Attributes.REJECTED));
			return rejectBeta;
		} else {
			//pass the processed content to the successor
			Attributes betaReceived = this.getPostSuccessor().post(message, alpha, beforePost);
			//do some actions with content returned by successor
			this.afterPost(message, alpha, betaReceived);
			//return the processed content
			return betaReceived;
		}
	}

	@Override
	public ResultContainer get(Query query) {
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
	 * Actions done on the post before passing it to the successor.
	 *
	 * @param message the message being posted
	 * @param alpha the attributes of the messages being posted
	 * @param beta the attributes to be added to the post
	 * @return the processed attributes
	 */
	protected Attributes beforePost(byte[] message, Attributes alpha, Attributes beta) {
		// default implementation
		return beta;
	}

	/**
	 * Actions done on the post after receiving it back from the successor
	 *
	 * @param message the message posted
	 * @param alpha the attributes of the messages posted
	 * @param beta the attributes added to the post
	 */
	protected void afterPost(byte[] message, Attributes alpha, Attributes beta) {
		// default implementation
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
	 * Returns the post successor layer in the layer stack
	 *
	 * @return the post service coming after this layer
	 */
	protected abstract PostService getPostSuccessor();

	/**
	 * Returns the get successor layer in the layer stack
	 *
	 * @return the get service coming after this layer
	 */
	protected abstract GetService getGetSuccessor();
}
