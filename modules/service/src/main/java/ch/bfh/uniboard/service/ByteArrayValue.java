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

import java.util.Arrays;

/**
 * Wrapper for the byte array variables
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class ByteArrayValue implements Value<byte[]> {

    private byte[] value;

    public ByteArrayValue(byte[] value) {
        this.value = value;
    }
    
    @Override
    public byte[] getValue() {
        return this.value;
    }

    @Override
    public void setValue(byte[] value) {
        this.value = value;
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
        return "ByteArrayType{" + "value=" + value + '}';
    }
    
}
