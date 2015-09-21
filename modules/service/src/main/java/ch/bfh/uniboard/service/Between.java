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
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class Between extends Constraint implements Serializable {

	private final Value start;
	private final Value end;

	public Between(Identifier identifier, Value start, Value end) {
		super(identifier);
		this.start = start;
		this.end = end;
	}

	public Value getStart() {
		return start;
	}

	public Value getEnd() {
		return end;
	}

	@Override
	public String toString() {
		return "Between{" + super.getIdentifier().toString() + "start=" + start + ", end=" + end + '}';
	}

}
