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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "between", propOrder = {
	"start",
	"end"
})
public class Between extends Constraint implements Serializable {

	@XmlElement(required = true)
	private Value start;
	@XmlElement(required = true)
	private Value end;

	public Between(Identifier identifier) {
		super(identifier);
	}

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
