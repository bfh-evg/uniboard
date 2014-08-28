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
 * Order allows to indicate the persistence layer what ordering one wishes for the result.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Order {

	private final Identifier identifier;
	//True indicates asc and false desc
	private final boolean ascDesc;

	public Order(Identifier identifier, boolean ascDesc) {
		this.identifier = identifier;
		this.ascDesc = ascDesc;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public boolean isAscDesc() {
		return ascDesc;
	}

}
