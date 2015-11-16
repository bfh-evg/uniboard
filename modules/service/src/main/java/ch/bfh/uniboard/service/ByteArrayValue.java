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
import java.util.Arrays;
import java.util.Base64;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Wrapper for the byte array variables
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "byteArrayValue", propOrder = {
	"value"
})
public class ByteArrayValue extends Value<byte[]> implements Serializable {

	@XmlElement(required = true)
	private byte[] value;

	public ByteArrayValue() {
		super();
	}

	public ByteArrayValue(byte[] value) {
		this.value = value;
	}

	@Override
	public byte[] getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Arrays.hashCode(this.value);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ByteArrayValue other = (ByteArrayValue) obj;
		if (!Arrays.equals(this.value, other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ByteArrayValue{" + "value=" + Base64.getEncoder().encodeToString(value) + '}';
	}

}
