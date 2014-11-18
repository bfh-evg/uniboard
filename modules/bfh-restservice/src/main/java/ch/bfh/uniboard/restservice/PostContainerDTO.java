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
package ch.bfh.uniboard.restservice;

import ch.bfh.uniboard.data.AttributesDTO;

/**
 * The class PostContainerDTO implements a container of a post request.
 *
 * @author Stephan Fischli &lt;stephan.fischli@bfh.ch&gt;
 */
public class PostContainerDTO {

	private String message; // Base64 encoded
	private AttributesDTO alpha;

	public PostContainerDTO() {
	}

	public PostContainerDTO(String message, AttributesDTO alpha) {
		this.message = message;
		this.alpha = alpha;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public AttributesDTO getAlpha() {
		return alpha;
	}

	public void setAlpha(AttributesDTO alpha) {
		this.alpha = alpha;
	}
}
