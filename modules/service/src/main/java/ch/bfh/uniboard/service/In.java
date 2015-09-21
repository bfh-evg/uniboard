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

import java.util.List;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class In extends Constraint {

	private final List<Value> element;

	public In(Identifier identifier, List<Value> element) {
		super(identifier);
		this.element = element;
	}

	public List<Value> getSet() {
		return element;
	}

	@Override
	public String toString() {
		return "In{" + super.getIdentifier().toString() + " element=" + element + '}';
	}

}
