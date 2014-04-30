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
 * Generic interface for a component of the bulletin board. It exposes two generic methods to clients. Clients are
 * assumed to provide all the necessary data in the arguments of the methods.
 * <p>
 * Calls to these methods are blocking. That is, client calls block until the taks of the method is completed.
 * <p>
 * TODO: Add @Local Annotation
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
public interface Service {

	/**
	 * Posts a message to the bulletin board by posting it to a component implementing this interface.
	 *
	 * @param message a message containing all required information elements
	 * @param alpha Atrributes added by the author of the message.
	 * @param beta Attributes added by upper services.
	 * @return a response
	 */
	public Attributes post(Message message, Attributes alpha, Attributes beta);

	/**
	 * Queries the bulletin board asking a component implementing this interface.
	 *
	 * @param query a query containing all required information elements
	 * @return a result
	 */
	public ResultContainer get(Query query);
}
