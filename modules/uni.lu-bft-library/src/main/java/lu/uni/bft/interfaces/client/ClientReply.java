/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.client;

import lu.uni.bft.interfaces.replica.BFTProtocolMessage;

/**
 *
 * @author rui.joaquim
 */
public interface ClientReply extends BFTProtocolMessage{
    /**
     * @return the client identifier.
     */
    public int getClientID();
    
    /**
     * @return the serial number assigned to the request by the client. 
     */
    public long getClientRequestSN();
    
    /**
     * @return the replica's signature on the operation and 
     *         corresponding result.
     */
    public byte[] getReplicaSignature();
}
