/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.uniboard.bft.service;

import java.io.Serializable;

/**
 * This class defines the message object that will be used in the client/replica
 * and replica/replica communication.
 * 
 * @author Rui Joaquim
 */
public class BFTServiceMessage implements Serializable{
    private static final long serialVersionUID = 1L;
        
    private byte messageType;
    private Serializable payload;
    private String senderID;
    private byte[] signature;
    
    /**
     * Method to obtain the message type. The value returned should be one of 
     * the values defined in the class BFTMessageType. 
     * @return the message type. 
     */
    public byte getMessageType(){return this.messageType;}
    
    /**
     * Returns the payload to which this message refers to. 
     * @return the message payload. 
     */
    public Serializable getPayload(){return this.payload;}
    
    /**
     * Gets the identifier of the sender (BFT Client or BFT Replica).
     * @return the identifier of the sender. 
     */
    public String getSenderID(){return this.senderID;}
    
    /**
     * Returns the signature of message sender on [message type|payload].
     * @TODO specify the signature format 
     * @return a signature in binary format. 
     */
    public byte[] getSignature(){return this.signature;}
}
