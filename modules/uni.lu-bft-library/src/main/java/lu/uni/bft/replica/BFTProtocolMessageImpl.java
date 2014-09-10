/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;

import lu.uni.bft.interfaces.replica.BFTProtocolMessage;

/**
 *
 * @author rui.joaquim
 */
public abstract class BFTProtocolMessageImpl implements BFTProtocolMessage{

    /** The number of the view to which this message belongs */
    protected long viewNumber;
    /** The request serial number within the view */
    protected long requestSN;
    /** The identifier of the replica that has created the message */
    protected int replicaID;
    
    /**
     * Constructor to be used by the subclasses that initializes 
     * the object fields.
     * 
     * @param view the view number
     * @param requestSN the request serial number
     * @param replica the replica ID
     */
    protected BFTProtocolMessageImpl(long view, long requestSN, int replica){
        this.viewNumber = view;
        this.requestSN = requestSN;
        this.replicaID = replica;
    }
    
    @Override
    public long getViewNumber() {
        return this.viewNumber;
    }
    
    @Override
    public long getViewRequestSN() {
        return this.requestSN;
    }
    
    @Override
    public int getReplicaID() {
        return this.replicaID;
    }
    
    
}
