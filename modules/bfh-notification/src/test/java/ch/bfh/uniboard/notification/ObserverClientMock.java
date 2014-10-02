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
package ch.bfh.uniboard.notification;

import ch.bfh.uniboard.data.PostDTO;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
@LocalBean
public class ObserverClientMock implements ObserverClient {

	private PostDTO post;

	@Override
	public void notifyObserver(String url, PostDTO post) {
		this.post = post;
	}

	public PostDTO getPost() {
		return post;
	}

	public void reset() {
		this.post = null;
	}
}
