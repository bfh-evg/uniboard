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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * A constraint allows to restrict the result retrieved from the persistence layer. A constraint consists of an
 * identifier of the element to restrict and other elements depending on the type of constraint.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "constraint", propOrder = {
	"identifier"
})
@XmlSeeAlso({
	In.class,
	NotEqual.class,
	Less.class,
	Equal.class,
	GreaterEqual.class,
	LessEqual.class,
	Greater.class,
	Between.class
})
public abstract class Constraint implements Serializable {

	@XmlElement(required = true)
	private Identifier identifier;
	private DataType dataType;

	public Constraint() {
		super();
	}

	public Constraint(Identifier identifier) {
		this.identifier = identifier;
	}

	public Constraint(Identifier identifier, DataType dataType) {
		this.identifier = identifier;
		this.dataType = dataType;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		this.dataType = dataType;
	}

}
