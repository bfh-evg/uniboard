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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "messageIdentifier", propOrder = {"keyPath", "dataType"})
public class MessageIdentifier extends Identifier {

	@XmlElement(required = true)
	protected String keyPath;
	@XmlElement(required = true)
	protected DataType dataType;

	/**
	 * Default no-arg constructor
	 *
	 */
	public MessageIdentifier() {
		super();
	}

	/**
	 * Fully-initialising value constructor
	 *
	 */
	public MessageIdentifier(final String keyPath, final DataType dataType) {
		super();
		this.keyPath = keyPath;
		this.dataType = dataType;
	}

	/**
	 * Gets the value of the keyPath property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getKeyPath() {
		return keyPath;
	}

	/**
	 * Sets the value of the keyPath property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setKeyPath(String value) {
		this.keyPath = value;
	}

	/**
	 * Gets the value of the dataType property.
	 *
	 * @return possible object is {@link DataTypeDTO }
	 *
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Sets the value of the dataType property.
	 *
	 * @param value allowed object is {@link DataTypeDTO }
	 *
	 */
	public void setDataType(DataType value) {
		this.dataType = value;
	}

}
