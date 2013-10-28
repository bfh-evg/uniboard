/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniVote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */

package ch.bfh.uniboard.service;

/**
 * Generic exception which can be raised between inter component communications.
 * It may be raised, for example, when the calling client does not provided
 * the requiered information elements required by the component implementing
 * the service.
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
public class UniBoardException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an exception instance and initalizes its
     * message field.
     *
     * @param reason a reason indicating the cause of this exception
     */
    public UniBoardException(String reason) {
        super(reason);
    }

    /**
     * Retunrs an indication of the cause of this message. Must
     * not be used for the application logic.
     *
     * @return a reason
     */
    public String getReason() {
        return super.getMessage();
    }
}
