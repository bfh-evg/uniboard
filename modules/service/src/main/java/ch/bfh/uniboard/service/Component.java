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
package ch.bfh.uniboard.service;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
public abstract class Component implements Service {

    @Override
    public final Response post(Message message) throws UniBoardException {
        Message beforePost = beforePost(message);
        Response response = getSuccessor().post(beforePost);
        Response afterPost = afterPost(response);
        return afterPost ;
    }

    @Override
    public final Result get(Query query) throws UniBoardException {
        Query beforeGet = beforeGet(query);
        Result result = getSuccessor().get(beforeGet);
        Result afterGet = afterGet(result);
        return afterGet;
    }

    protected Message beforePost(Message message) throws UniBoardException {
        // default implementation
        return message;
    }

    protected Response afterPost(Response response) throws UniBoardException {
        // default implementation
        return response;
    }

    protected Query beforeGet(Query query) throws UniBoardException {
        // default implementation
        return query;
    }

    protected Result afterGet(Result result) throws UniBoardException {
        // default implementation
        return result;
    }

    protected abstract Service getSuccessor();
}
