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
import lu.uni.uniboard.bft.service.BFTComponentService;
import lu.uni.uniboard.bft.service.BFTServiceMessage;

/**
 *
 * @author Rui Joaquim
 */
@Stateless
public class BFTReplicaBean implements BFTComponentService {

    @Override
    public void process(BFTServiceMessage bftMessage) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
