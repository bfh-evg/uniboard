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
package lu.uni.uniboard.bft.service.proxy;

import java.io.Serializable;

/**
 * This interface defines application entry point for the BFT library. 
 * 
 * @author Rui Joaquim
 */
public interface BFTReplicationService {
  
   /**
    * This method is invoked by the application client to send a request to the 
    * replicated service through the BFT library. Both the input and the output 
    * of this method must implement the Serializable interface because they are 
    * used directly in the messages send between the BFT service components.
    * 
    * @param request the application level request to be processed.
    * @return the BFT replicas agreed result on the request.
    */ 
   public Serializable processRequest (Serializable request);
}
