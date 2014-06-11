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

import javax.ejb.Local;

/**
 * Generic interface for a component of the bulletin board. It exposes two generic methods to clients. Clients are
 * assumed to provide all the necessary data in the arguments of the methods.
 * <p>
 * Calls to these methods are blocking. That is, client calls block until the task of the method is completed.
 * <p>
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Local
public interface PostService {

	/**
	 * Posts a message to the bulletin board by posting it to a component implementing this interface.
	 *
	 * @param message a message containing all required information elements
	 * @param alpha Attributes added by the author of the message.
	 * @param beta Attributes added by upper services.
	 * @return a response
	 */
	public Attributes post(byte[] message, Attributes alpha, Attributes beta);

}
