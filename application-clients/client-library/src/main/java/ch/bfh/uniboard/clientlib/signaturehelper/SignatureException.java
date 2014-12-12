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
package ch.bfh.uniboard.clientlib.signaturehelper;

/**
 * Exception thrown during generation or validation of signatures
 * @author Philémon von Bergen
 */
public class SignatureException extends Exception {
    
    public SignatureException(String message){
	super(message);
    }
}