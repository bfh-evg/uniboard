/*
 * Uniboard
 *
 *  Copyright (c) 2016 Bern University of Applied Sciences (BFH),
 *  Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniVote2 may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH),
 *   Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: severin.hauser@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.uniboard.service.data;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
	"key",
	"value",
	"dataType"
})
public class Attribute {

	@XmlElement(required = true)
	protected String key;
	@XmlElement(required = true)
	protected String value;
	protected DataType dataType;

	/**
	 * Fully-initialising value constructor
	 *
	 * @param key
	 * @param value
	 * @param dataType
	 */
	public Attribute(final String key, final String value, final DataType dataType) {
		this.key = key;
		this.value = value;
		this.dataType = dataType;
	}

	public Attribute(String key, String value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * Gets the value of the key property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the value of the value property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the value of the dataType property.
	 *
	 * @return possible object is {@link DataType }
	 *
	 */
	public DataType getDataType() {
		return dataType;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Attribute other = (Attribute) obj;
		if (!Objects.equals(this.key, other.key)) {
			return false;
		}
		if (!Objects.equals(this.value, other.value)) {
			return false;
		}
		return this.dataType == other.dataType;
	}

}
