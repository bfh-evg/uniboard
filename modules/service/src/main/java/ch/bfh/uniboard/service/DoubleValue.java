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

import java.util.Objects;

/**
 * Wrapper for double variables
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class DoubleValue implements Value<Double> {

	private Double value;

	public DoubleValue(Double value) {
		this.value = value;
	}


	@Override
	public Double getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + Objects.hashCode(this.value);
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
		final DoubleValue other = (DoubleValue) obj;
		if (!Objects.equals(this.value, other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DoubleValue{" + "value=" + value + '}';
	}

}
