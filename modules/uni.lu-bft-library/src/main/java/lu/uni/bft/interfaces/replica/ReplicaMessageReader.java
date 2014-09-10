/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

import lu.uni.bft.interfaces.replica.BFTProtocolMessage;

/**
 * This interface abstract the replica-to-replica input communication, 
 * and thus decouple of the BFT core from the communication layer.
 *
 * @author rui.joaquim@uni.lu
 */
public interface ReplicaMessageReader {
    /**
     * This method is used to obtain the next protocol message from a 
     * BFT service replica. This method can block while waiting for a 
     * 
     * @return The next protocol message from the replica or null if there is 
     * no message available or if there is an error .
     */
    public BFTProtocolMessage getNextMessage();
}
