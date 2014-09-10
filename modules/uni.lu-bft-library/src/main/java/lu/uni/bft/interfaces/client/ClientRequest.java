/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.client;

import java.io.Serializable;

/**
 * TODO: Define how to get the byte array representation of the operation. This 
 *       will be needed to verify the client signature.
 * 
 * 
 * @author rui.joaquim@uni.lu
 * 
 * @param <T> The type of the operation requested by the client. The operation
 *              type must be serializable. 
 */
public interface ClientRequest<T extends Serializable> {
    /** 
     * @return the Id of the client that made the operation request.
     */
    public int getClientID();
    
    /**
     * @return the client request serial number. This serial number is 
     *          maintained by the client and should be incremented by one for
     *          on each issued request.
     */
    public long getClientRequestSN();
    
    /**
     * @return the requested operation.
     */
    public T getOperation();
    
    /**
     * @return the client signature on the requested operation.
     */
    public byte[] getSignature();
}
