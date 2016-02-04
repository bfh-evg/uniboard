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
 * Order allows to indicate the persistence layer what ordering one wishes for the result.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order", propOrder = {
	"identifier",
	"ascDesc"
})
public class Order implements Serializable {

	@XmlElement(required = true)
	private Identifier identifier;
	//True indicates asc and false desc
	private boolean ascDesc;

	public Order() {
	}

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
