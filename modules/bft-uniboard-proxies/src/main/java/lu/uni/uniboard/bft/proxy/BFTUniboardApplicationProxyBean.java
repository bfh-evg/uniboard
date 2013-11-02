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
package lu.uni.uniboard.bft.proxy;

import java.io.Serializable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import lu.uni.uniboard.bft.service.proxy.BFTReplicatedService;

/**
 * This class mediates the BFT runtime and the Uniboard application.
 * 
 * @author Rui Joaquim
 */
@Stateless
@Local(value = BFTReplicatedService.class)
public class BFTUniboardApplicationProxyBean implements BFTReplicatedService {

    /**
     * Forwards the request to the Uniboard application and return the result of
     * the given by the Uniboard application.
     * 
     * @param request the request to be processed by the Uniboard application.
     * @return the result given by the Uniboard application.
     */
    @Override
    public Serializable processRequest(Serializable request) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }

  
}
