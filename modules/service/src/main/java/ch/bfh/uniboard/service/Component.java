/*
 * Copyright (c) 2014 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
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
    public final Attributes post(String application, Message message, Attributes alpha, Attributes beta) throws UniBoardException {
        //Do some preprocessing actions
        Attributes beforePost = beforePost(application, message, alpha, beta);
        //pass the processed content to the successor
        Attributes betaReceived = getSuccessor().post(application, message, alpha, beforePost);
        
        Attributes afterPost = afterPost(application, message, alpha, betaReceived);
        return afterPost ;
    }

    @Override
    public final ResultContainer get(String application, Query query) throws UniBoardException {
        //do some verification actions
        beforeGet(application, query);
        //pass content received to the successor
        ResultContainer resultContainer = getSuccessor().get(application, query);
        //do some actions with content returned by successor
        Attributes newGamma = afterGet(application, query, resultContainer);
        //put the attributes processed in the previously received resultContainer
        resultContainer.setGamma(newGamma);
        //return the result container
        return resultContainer;
    }

    
    /**
     * Actions done on the query before passing it to the successor.
     * @param application
     * @param message
     * @param alpha
     * @param beta
     * @return the processed attributes
     * @throws UniBoardException 
     */
    protected Attributes beforePost(String application, Message message, Attributes alpha, Attributes beta) throws UniBoardException {
        // default implementation
        beta.
        return beta;
    }

    protected Attributes afterPost(String application, Message message, Attributes alpha, Attributes beta) throws UniBoardException {
        // default implementation
        return beta;
    }

    /**
     * Actions done on the query before passing it to the successor. This method can only do verifications
     * but cannot add information
     * @param application the application identifier
     * @param query the query to fulfill
     * @throws UniBoardException if service request could not be fulfilled
     */
    protected void beforeGet(String application, Query query) throws UniBoardException {
        // default implementation
        return;
    }

    /**
     * Actions done after receiving the result from the successor. Changes can only be done in the attributes
     * @param application the application identifier
     * @param query the query to fulfill
     * @param resultContainer the container with the result to the query and the attributes
     * @return the attributes processed
     * @throws UniBoardException 
     */
    protected Attributes afterGet(String application, Query query, ResultContainer resultContainer) throws UniBoardException {
        // default implementation
        return resultContainer.getGamma();
    }

    protected abstract Service getSuccessor();
}
