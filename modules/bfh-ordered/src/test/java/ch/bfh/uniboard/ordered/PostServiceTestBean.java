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
package ch.bfh.uniboard.ordered;

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.data.Attribute;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@LocalBean
public class PostServiceTestBean implements PostService {

	private ch.bfh.uniboard.service.data.Post lastPost;

	private boolean error = false;

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		this.lastPost = new ch.bfh.uniboard.service.data.Post();
		this.lastPost.setAlpha(alpha);
		this.lastPost.setMessage(message);
		if (error) {
			beta.add(new Attribute(Attributes.ERROR, "1"));
		}
		return beta;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public ch.bfh.uniboard.service.data.Post getLastPost() {
		return lastPost;
	}
}
