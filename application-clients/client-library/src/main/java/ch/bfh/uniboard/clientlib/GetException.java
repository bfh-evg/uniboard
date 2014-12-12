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

/**
 * Exception thrown during preparation of getting or during validation of board signature
 * @author Philémon von Bergen
 */
public class GetException extends Exception {

    public GetException(String error) {
	super(error);
    }
    
}
