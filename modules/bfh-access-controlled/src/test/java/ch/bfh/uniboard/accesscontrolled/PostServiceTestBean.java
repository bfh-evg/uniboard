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
package ch.bfh.uniboard.accesscontrolled;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.PostService;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@LocalBean
public class PostServiceTestBean implements PostService {

	private ch.bfh.uniboard.service.Post lastPost;

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		this.lastPost = new ch.bfh.uniboard.service.Post();
		this.lastPost.setAlpha(alpha);
		this.lastPost.setMessage(message);
		this.lastPost.setBeta(beta);
		return beta;
	}

	public ch.bfh.uniboard.service.Post getLastPost() {
		ch.bfh.uniboard.service.Post retunPost = this.lastPost;
		this.lastPost = null;
		return retunPost;
	}
}
