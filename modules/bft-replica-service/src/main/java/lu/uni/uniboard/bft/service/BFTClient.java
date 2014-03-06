/*
 * Copyright (c) 2014 ... Rui?
 *
 * Project VIVO.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package lu.uni.uniboard.bft.service;



/**
 * This interface defines the entry point of a BFT client.
 * <p>
 * TODO: Envision to eleminate this interface. See also "ch.bfh.uniboard.service.Service".
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
