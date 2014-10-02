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
 * A constraint allows to restrict the result retrieved from the persistence layer. A constraint consists of an
 * identifier of the element to restrict and other elements depending on the type of constraint.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public abstract class Constraint implements Serializable {

	private final Identifier identifier;

	public Constraint(Identifier identifier) {
		this.identifier = identifier;
	}

	public Identifier getIdentifier() {
		return identifier;
	}
}
