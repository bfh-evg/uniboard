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
package lu.uni.uniboard.bft.replica;


import javax.ejb.Stateless;
import lu.uni.uniboard.bft.service.BFTReplica;
import lu.uni.uniboard.bft.service.BFTServiceMessage;

/**
 * This class is the entry point for BFT protocol messages at the BFT 
 * replica side.
 * 
 * @author Rui Joaquim
 */
@Stateless
public class BFTReplicaBean implements BFTReplica {

    /**
     * This method receives a BFT protocol message to be processed by the
     * BFT replica
     * 
     * @param bftMessage the message to process 
     */
    @Override
    public void processMessage(BFTServiceMessage bftMessage) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
