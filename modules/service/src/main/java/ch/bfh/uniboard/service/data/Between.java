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
package ch.bfh.uniboard.service.data;

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
	private String lowerBound;
	@XmlElement(required = true)
	private String upperBound;

	public Between() {
	}

	public Between(Identifier identifier) {
		super(identifier);
	}

	public Between(Identifier identifier, String lowerBound, String upperBound) {
		super(identifier);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public Between(Identifier identifier, String lowerBound, String upperBound, DataType dataType) {
		super(identifier, dataType);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public String getLowerBound() {
		return lowerBound;
	}

	public String getUpperBound() {
		return upperBound;
	}

	@Override
	public String toString() {
		return "Between{" + super.getIdentifier().toString()
				+ "lowerBound=" + lowerBound + ", upperBound=" + upperBound + '}';
	}

}
