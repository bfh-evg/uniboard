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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * A Identifier is used by the persistence layer to identify an element. As the the top tier elements are fixed by
 * UniBoard, there are Implementations that indicate that.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "identifier", propOrder = {
	"part"
})
@XmlSeeAlso({
	BetaIdentifier.class,
	AlphaIdentifier.class,
	MessageIdentifier.class
})
public abstract class Identifier implements Serializable {

	private List<String> part;

	public Identifier() {
		part = new ArrayList<>();
	}

	public Identifier(List<String> parts) {
		this.part = parts;
	}

	public Identifier(String[] identifier) {
		part = new ArrayList<>();
		part.addAll(Arrays.asList(identifier));
	}

	public Identifier(String identifier) {
		this(identifier.split("\\."));
	}

	public List<String> getParts() {
		return part;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String tmpPart : part) {
			if (builder.length() > 0) {
				builder.append(".");
			}
			builder.append(tmpPart);
		}
		return builder.toString();
	}

}
