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

import ch.bfh.uniboard.service.data.Attributes;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public abstract class PostComponent implements PostService {

	@Override
	public final Attributes post(byte[] message, Attributes alpha, Attributes beta) {
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
	 * Returns the post successor layer in the layer stack
	 *
	 * @return the post service coming after this layer
	 */
	protected abstract PostService getPostSuccessor();

}
