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
package ch.bfh.uniboard.clientlib;

import ch.bfh.uniboard.data.AttributesDTO;
import java.util.Iterator;

/**
 * Class containing helper methods for getting attributes
 *
 * @author Philémon von Bergen
 */
public class AttributeHelper {

    /**
     * Search an attribute with key "key" in the list of given attributes
     * @param attributes the attributes in which to search
     * @param key the key of the attributes to search
     * @return the attribute if one was found, null otherwise
     */
    public static AttributesDTO.AttributeDTO searchAttribute(AttributesDTO attributes, String key) {
        Iterator<AttributesDTO.AttributeDTO> it = attributes.getAttribute().iterator();
        AttributesDTO.AttributeDTO attr;
        while (it.hasNext()) {
            attr = it.next();
            if (attr.getKey().equals(key)) {
                return attr;
            }
        }
        return null;
    }
}
