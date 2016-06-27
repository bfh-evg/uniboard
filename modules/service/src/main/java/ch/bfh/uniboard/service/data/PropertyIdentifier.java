package ch.bfh.uniboard.service.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Identifier used for alpha and beta attributes.
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "propertyIdentifier", propOrder = {
	"propertyType",
	"keyPath"
})
public class PropertyIdentifier
		extends Identifier {

	@XmlElement(required = true)
	protected PropertyIdentifierType propertyType;
	@XmlElement(required = true)
	protected String keyPath;

	/**
	 * Default no-arg constructor
	 *
	 */
	public PropertyIdentifier() {
		super();
	}

	/**
	 * Fully-initialising value constructor
	 *
	 */
	public PropertyIdentifier(final PropertyIdentifierType type, final String keyPath) {
		super();
		this.propertyType = type;
		this.keyPath = keyPath;
	}

	/**
	 * Gets the value of the type property.
	 *
	 * @return possible object is {@link PropertyIdentifierType }
	 *
	 */
	public PropertyIdentifierType getType() {
		return propertyType;
	}

	/**
	 * Sets the value of the type property.
	 *
	 * @param value allowed object is {@link PropertyIdentifierType }
	 *
	 */
	public void setType(PropertyIdentifierType value) {
		this.propertyType = value;
	}

	/**
	 * Gets the value of the key property.
	 *
	 * @return possible object is {@link String }
	 *
	 */
	public String getKeyPath() {
		return keyPath;
	}

	/**
	 * Sets the value of the key property.
	 *
	 * @param value allowed object is {@link String }
	 *
	 */
	public void setKeyPath(String value) {
		this.keyPath = value;
	}

}
