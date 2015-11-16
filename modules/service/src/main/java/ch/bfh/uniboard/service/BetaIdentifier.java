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
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "betaIdentifier")
public class BetaIdentifier extends Identifier implements Serializable {

	public BetaIdentifier() {
		super();
	}

	public BetaIdentifier(List<String> parts) {
		super(parts);
	}

	public BetaIdentifier(String[] identifier) {
		super(identifier);
	}

	public BetaIdentifier(String identifier) {
		super(identifier);
	}

}
