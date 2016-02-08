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

import java.util.List;
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
@XmlType(name = "in", propOrder = {
	"element"
})
public class In extends Constraint {

	@XmlElement(required = true)
	private List<String> element;

	public In() {
		super();
	}

	public In(Identifier identifier, List<String> element) {
		super(identifier);
		this.element = element;
	}

	public In(Identifier identifier, List<String> element, DataType dataType) {
		super(identifier, dataType);
		this.element = element;
	}

	public List<String> getSet() {
		return element;
	}

	@Override
	public String toString() {
		return "In{" + super.getIdentifier().toString() + " element=" + element + '}';
	}

}
