/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

import lu.uni.bft.replica.CommunicationException;
import lu.uni.bft.interfaces.replica.ClientRequestWithAgreement;

/**
 * Interface that links the core of the replica BFT library 
 * to the replicated service.
 *
 * @author rui.joaquim@uni.lu
 */
public interface ReplicatedService {
    /**
     * This method is used to send an agreed request to the service for 
     * processing. The processing of the request includes sending the reply 
     * to the corresponding client, which decouple the BFT core from the 
     * communications layer.
     * 
     * @param request The request to be processed by the service. 
     * @throws lu.uni.bft.protocol.CommunicationException if an error occurs 
     *         while fetching the client message.
     */
    public void processMessage(ClientRequestWithAgreement request) 
            throws CommunicationException;
}
