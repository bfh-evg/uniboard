/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

/**
 * This interface extends the BFTProtocolMessage interface with the 
 * functionality specific to a COMMIT message.
 * 
 * @author rui.joaquim
 */
public interface CommitMessage extends BFTProtocolMessage{
    /* At the moment there is no specific behavior to be specified. Thus, this
       interface is currently used only to define a message type. 
    */
}
