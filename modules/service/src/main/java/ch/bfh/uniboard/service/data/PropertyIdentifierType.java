
package ch.bfh.uniboard.service.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for simpleIdentifierType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="simpleIdentifierType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="alpha"/>
 *     &lt;enumeration value="beta"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "simpleIdentifierType")
@XmlEnum
public enum PropertyIdentifierType {

    @XmlEnumValue("alpha")
    ALPHA("alpha"),
    @XmlEnumValue("beta")
    BETA("beta");
    private final String value;

    PropertyIdentifierType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PropertyIdentifierType fromValue(String v) {
        for (PropertyIdentifierType c: PropertyIdentifierType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
