/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

import lu.uni.bft.interfaces.replica.BFTProtocolMessage;

/**
 * This interface abstract the replica-to-replica output communication, 
 * and thus decouple the BFT core from the communication layer.
 *
 * @author rui.joaquim@uni.lu
 */
public interface ReplicaMessagePublication {
    /**
     * Method to send a BFTProtocolMessage to all replicas.
     * @param msg the message to be send.
     */
    public void sendAll(BFTProtocolMessage msg);
}
