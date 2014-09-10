/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

import java.io.Serializable;

/**
 * The BFTProtocolMessage interface defines the basic functionalities of the 
 * messages changed by the replicas in the BFT protocol.
 * 
 * @author rui.joaquim
 */
public interface BFTProtocolMessage extends Serializable {
   
    /**
     * @return the view number in which the message was created.
     */
    public long getViewNumber();
    
    /**
     * @return serial number in the view that was assigned to the request
     *          to which the message refers to. 
     */
    public long getViewRequestSN();
    
    /**
     * @return the ID of the replica that created the message.
     */
    public int getReplicaID();
}
