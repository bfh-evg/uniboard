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
import lu.uni.bft.replica.service.BFTReplicaException;
import lu.uni.bft.replica.service.BFTReplicaService;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless
public class BFTReplicaBean implements BFTReplicaService {

    @Override
    public Object submit(Object o) throws BFTReplicaException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        return null;
    }

    
}
