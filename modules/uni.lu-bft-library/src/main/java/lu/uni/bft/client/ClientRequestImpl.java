/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.client;

import java.io.Serializable;
import lu.uni.bft.interfaces.client.ClientRequest;

/**
 *
 * @author rui.joaquim
 * @param <T> The type of the operations supported by the service. 
 */
public class ClientRequestImpl <T extends Serializable> 
                                                implements ClientRequest<T> {
    /** The client identifier. */
    protected final int clientID;
    /** The serial number given to the request by the client */
    protected final long clientRequestSN;
    /** The actual operation to perform. */
    protected final T operation;
    /** The client signature on the request. */
    protected final byte[] signature;
    
    /**
     * @param client the client identifier.
     * @param requestSN the serial number given by the client to the request.
     * @param operation the actual operation to be performed.
     * @param signature the client signature on the request.
     */
    public ClientRequestImpl(
            int client, long requestSN, T operation, byte[] signature){
        
        this.clientID = client;
        this.clientRequestSN = requestSN;
        this.operation = operation;
        this.signature = signature;
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
    public T getOperation() {
        return this.operation;
    }

    @Override
    public byte[] getSignature() {
        return this.signature;
    }
    
}
