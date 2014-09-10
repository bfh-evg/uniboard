/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;

import java.io.Serializable;
import lu.uni.bft.client.ClientRequestImpl;
import lu.uni.bft.interfaces.client.ClientRequest;
import lu.uni.bft.interfaces.replica.VerifiedClientRequest;

/**
 *
 * @author rui.joaquim
 * 
 * @param <T> the type of the class that represents the requested operations.
 */
public class VerifiedClientRequestImpl<T extends Serializable>
               extends ClientRequestImpl<T> implements VerifiedClientRequest<T>{

    /** The hash value of the verified request. */
    protected final byte[] requestHashValue;
    
    /**
     * @param request the request that was verified.
     * @param hash the hash value of the verified request.
     */
    public VerifiedClientRequestImpl(ClientRequest<T> request, byte[] hash) {
        super(request.getClientID(), request.getClientRequestSN(), 
                request.getOperation(), request.getSignature());
        
        this.requestHashValue = hash;
    }

    @Override
    public byte[] getCryptographicHash() {
        return this.requestHashValue;
    }
      
}
