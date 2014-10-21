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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A Identifier is used by the persistence layer to identify an element. As the the top tier elements are fixed by
 * UniBoard, there are Implementations that indicate that.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public abstract class Identifier implements Serializable {

	private List<String> parts;

	public Identifier() {
		parts = new ArrayList<>();
	}

	public Identifier(List<String> parts) {
		this.parts = parts;
	}

	public Identifier(String[] identifier) {
		parts = new ArrayList<>();
		parts.addAll(Arrays.asList(identifier));
	}

	public Identifier(String identifier) {
		this(identifier.split("\\."));
	}

	public List<String> getParts() {
		return parts;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String part : parts) {
			if (builder.length() > 0) {
				builder.append(".");
			}
			builder.append(part);
		}
		return builder.toString();
	}

}
