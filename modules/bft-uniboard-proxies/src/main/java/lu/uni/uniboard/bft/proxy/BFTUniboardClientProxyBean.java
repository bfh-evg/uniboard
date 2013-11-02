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
import ch.bfh.uniboard.service.Service;
import ch.bfh.uniboard.service.UniBoardException;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 * This class mediates the interaction between the "Uniboard client"  
 * and the BFT runtime. 
 * 
 * @author Rui Joaquim
 */
@Stateless
@Local(value = Service.class)
public class BFTUniboardClientProxyBean implements Service {

    /**
     * Forwards a post request to the BFT runtime and returns the agreed result.
     * 
     * @param message the message to be posted on the Uniboard.
     * @return the Replicas agreed result of the post.
     * 
     * @TODO specify 
     * @throws UniBoardException 
     */
    @Override
    public Response post(Message message) throws UniBoardException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Response r = new Response(null);
        return r;
    }

    /**
     * Forwards a get request to the BFT runtime and returns the agreed result.
     * 
     * @param query the query to be answered by the Uniboard.
     * @return the Replicas agreed result of the query.
     * 
     * @TODO specify 
     * @throws UniBoardException 
     */
    @Override
    public Result get(Query query) throws UniBoardException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        Result r = new Result(null);
        return r;
    }

  
}
