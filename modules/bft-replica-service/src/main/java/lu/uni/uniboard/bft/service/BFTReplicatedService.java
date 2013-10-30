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
package lu.uni.uniboard.bft.service;

import java.io.Serializable;

/**
 * This interface must be implemented by the proxy that will mediate the 
 * interaction BFT between the BFT replica service and the actual service being 
 * replicated. 
 * 
 * @author Rui Joaquim
 */
public interface BFTReplicatedService {
   
   /**
    * This method will be invoked by the BFT replica service whenever there is
    * an agreed request to be processed. Both the input and the output of this 
    * method must implement the Serializable interface because they are used 
    * directly in the messages send between the BFT service components.
    * 
    * @param request the request to process.
    * @return the result of the requested operation.
    */ 
   public Serializable processRequest (Serializable request);
}
