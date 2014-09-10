/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

/**
 * This interface extends the BFTProtocolMessage interface with the 
 * functionality specific to a PREPARE message.
 * 
 * @author rui.joaquim
 */
public interface PrepareMessage {
    
    /**
     * @return the hash of the client request.
     */
    public byte[] getRequestHash();
}
