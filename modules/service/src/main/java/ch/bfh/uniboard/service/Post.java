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

import java.io.Serializable;

/**
 * A post represents a posted message and all belonging attributes.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Post implements Serializable {

	private static final long serialVersionUID = 1L;

	private byte[] message;
	private Attributes alpha;
	private Attributes beta;

	public byte[] getMessage() {
		return message;
	}

	public Attributes getAlpha() {
		return alpha;
	}

	public Attributes getBeta() {
		return beta;
	}
}
