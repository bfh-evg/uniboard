/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.client;

import lu.uni.bft.interfaces.client.ClientReply;

/**
 *
 * @author rui.joaquim
 */
public class ClientReplyImpl implements ClientReply{
    /** The ID of the client that made the request */
    protected final int clientID;
    /** The serial number given by the client to the request. */
    protected final long clientRequestSN;
    /** The view number in which the agreement was made. */
    protected final long viewNumber;
    /** The serial number given to the request in the view. */
    protected final long viewRequestSN;
    /** The ID of the replica that created the reply. */
    protected final int replicaID;
    /** The replica's signature on the reply. */
    protected final byte[] replicaSignature;
    
    
    /**
     * 
     * @param clientID ID of the client that made the request
     * @param clientRequestSN serial number given by the client to the request.
     * @param viewNumber view number in which the agreement was made.
     * @param viewRequestSN serial number given to the request in the view.
     * @param replicaID ID of the replica that created the reply.
     * @param signature replica's signature on the reply.
     */
    public ClientReplyImpl(int clientID, long clientRequestSN, long viewNumber,
            long viewRequestSN, int replicaID, byte[] signature){
        
        this.clientID = clientID;
        this.clientRequestSN = clientRequestSN;
        this.viewNumber = viewNumber;
        this.viewRequestSN = viewRequestSN;
        this.replicaID = replicaID;
        this.replicaSignature = signature;
    }
    
    @Override
    public int getClientID() {
        return this.clientID;
    }

    @Override
    public long getClientRequestSN() {
        return this.clientRequestSN;
    }

    @Override
    public byte[] getReplicaSignature() {
        return this.replicaSignature;
    }

    @Override
    public long getViewNumber() {
        return this.viewNumber;
    }

    @Override
    public long getViewRequestSN() {
        return this.viewRequestSN;
    }

    @Override
    public int getReplicaID() {
        return this.replicaID;
    }
    
}
