/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

import lu.uni.bft.interfaces.client.ClientRequest;

/**
 * This interface extends the BFTProtocolMessage interface with the 
 * functionality specific to a PRE-PREPARE message.
 * 
 * @author rui.joaquim
 */
public interface PrePrepareMessage extends BFTProtocolMessage{
    
    /**
     * @return the request issued by the client. 
     */
    public ClientRequest getRequest();
    
}
