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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A data container for the result of a query and some other attributes. Values should be immutable.
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "resultContainer", propOrder = {
	"result",
	"gamma"
})
public class ResultContainer implements Serializable {

	@XmlElement(required = true)
	private List<Post> result;
	@XmlElement(required = true)
	private Attributes gamma;

	public ResultContainer() {
	}

	/**
	 * Initialize the data container
	 *
	 * @param result the Result object containing the result of the query
	 * @param gamma additional attributes
	 */
	public ResultContainer(List<Post> result, Attributes gamma) {
		this.result = result;
		this.gamma = gamma;
	}

	/**
	 * Returns the Result object of the query
	 *
	 * @return a Result object
	 */
	public List<Post> getResult() {
		return result;
	}

	/**
	 * Get the additional attributes
	 *
	 * @return the object containing additional attributes
	 */
	public Attributes getGamma() {
		return gamma;
	}

	/**
	 * Set the additional attributes
	 *
	 * @param gamma the object containing additional attributes
	 */
	public void setGamma(Attributes gamma) {
		this.gamma = gamma;
	}
}
