package lu.uni.uniboard.bft.service;

import lu.uni.uniboard.bft.service.BFTServiceMessage;

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


/**
 * This interface defines the entry point of a BFT client.
 * 
 * @author Rui Joaquim
 */
public interface BFTClient {
    /**
     * Method to send a BFT protocol message to a client.
     * @param bftMessage
     */
   public void processMessage (BFTServiceMessage bftMessage);
}
