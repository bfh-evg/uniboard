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
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class NotEqual extends Constraint {

	private final Value value;

	public NotEqual(Identifier identifier, Value value) {
		super(identifier);
		this.value = value;
	}

	public Value getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "NotEqual{" + super.getIdentifier().toString() + " value=" + value + '}';
	}

}
