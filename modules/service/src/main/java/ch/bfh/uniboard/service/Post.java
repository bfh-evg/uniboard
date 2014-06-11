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

/**
 * A post represents a posted message and all belonging attributes.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    protected byte[] message;
    protected Attributes alpha;
    protected Attributes beta;

    public byte[] getMessage() {
    	return message;
    }

    public Attributes getAlpha() {
    	return alpha;
    }

    public Attributes getBeta() {
    	return beta;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    public void setAlpha(Attributes alpha) {
        this.alpha = alpha;
    }

    public void setBeta(Attributes beta) {
        this.beta = beta;
    }
        
        
}
