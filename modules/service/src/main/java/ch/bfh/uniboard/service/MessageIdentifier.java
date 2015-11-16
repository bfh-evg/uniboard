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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageIdentifier")
public class MessageIdentifier extends Identifier {

	public MessageIdentifier() {
		super();
	}

	public MessageIdentifier(List<String> parts) {
		super(parts);
	}

	public MessageIdentifier(String[] identifier) {
		super(identifier);
	}

	public MessageIdentifier(String identifier) {
		super(identifier);
	}

}
