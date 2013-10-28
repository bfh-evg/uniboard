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
package ch.bfh.uniboard.z.ejb.component1;

import ch.bfh.uniboard.service.Message;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.Response;
import ch.bfh.uniboard.service.Result;
import ch.bfh.uniboard.service.Service;
import ch.bfh.uniboard.service.UniBoardException;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless
@Local(value = Service.class)
public class TerminatingComponentBean implements Service {

    @Override
    public Response post(Message message) throws UniBoardException {
        System.out.println("post() called.");
        Response r = new Response(null);
        return r;
    }

    @Override
    public Result get(Query query) throws UniBoardException {
        System.out.println("get() called.");
        Result r = new Result(null);
        return r;
    }
}
