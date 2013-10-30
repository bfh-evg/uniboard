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

import ch.bfh.uniboard.service.Message;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.Response;
import ch.bfh.uniboard.service.Result;
import ch.bfh.uniboard.service.UniBoardException;
import java.io.Serializable;
import javax.ejb.Local;
import javax.ejb.Stateless;
import lu.uni.uniboard.bft.service.BFTReplicatedService;

/**
 *
 * @author Rui Joaquim
 */
@Stateless
@Local(value = BFTReplicatedService.class)
public class BFTApplicationProxyBean implements BFTReplicatedService {

    @Override
    public Serializable processRequest(Serializable request) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

  
}
