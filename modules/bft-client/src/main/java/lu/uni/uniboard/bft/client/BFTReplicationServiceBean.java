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
package lu.uni.uniboard.bft.client;

import java.io.Serializable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import lu.uni.uniboard.bft.service.proxy.BFTReplicationService;

/**
 * This class is the entry point to the BFT runtime.
 * 
 * @author Rui Joaquim
 */
@Stateless
@Local(value = BFTReplicationService.class)
public class BFTReplicationServiceBean implements BFTReplicationService {

    /**
     * Forwards the request to the BFT runtime and return the result agreed by 
     * the BFT replicas.
     * 
     * @param request the request to be processed through the BFT runtime.
     * @return the result agreed by the replicas.
     */
    @Override
    public Serializable processRequest(Serializable request) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }
  
}
