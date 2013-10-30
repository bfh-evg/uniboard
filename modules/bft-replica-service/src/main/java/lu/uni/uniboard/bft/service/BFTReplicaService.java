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
package lu.uni.uniboard.bft.service;

/**
 * This interface defines the entry point of a BFT replica.
 * 
 * @author Rui Joaquim
 */
public interface BFTReplicaService {
    /**
     * Method to send a BFT protocol message to a replica. 
     * @param bftMessage the message to be processed by the BFT replica.
     */
   public void processMessage (BFTServiceMessage bftMessage);
}
