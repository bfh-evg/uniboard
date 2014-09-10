/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

import lu.uni.bft.replica.CommunicationException;
import lu.uni.bft.interfaces.client.ClientRequest;

/**
 * This interface abstracts the client-to-replica communication, 
 * and thus decouple of the BFT core from the communication layer.
 *
 * @author rui.joaquim@uni.lu
 */
public interface ClientRequestReader {
    /**
     * The getNextRequest method is used to obtain a new client request. This 
     * method blocks while waiting for for a new client request to be 
     * available.
     * 
     * @return the next client request.
     * @throws lu.uni.bft.protocol.CommunicationException if an error occurs 
     * while fetching the client message.
     */
    public ClientRequest getNextRequest() throws CommunicationException;
}
