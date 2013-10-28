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
package ch.bfh.uniboard.z.ejb.compfinal;

import ch.bfh.uniboard.service.Message;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.Response;
import ch.bfh.uniboard.service.Result;
import ch.bfh.uniboard.service.Service;
import ch.bfh.uniboard.service.UniBoardException;
import java.util.logging.Logger;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless
@Local(value = Service.class)
public class ComponentFinalBean implements Service {
    private static final Logger logger = Logger.getLogger(ComponentFinalBean.class.getName());

    @Override
    public Response post(Message message) throws UniBoardException {
        logger.fine("***** post(Message) called *****");
        return new Response(null);
    }

    @Override
    public Result get(Query query) throws UniBoardException {
        logger.fine("***** get(Query) called *****");
        return new Result(null);
    }
}
