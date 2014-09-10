/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.interfaces.replica;

import java.io.Serializable;
import lu.uni.bft.interfaces.client.ClientRequest;

/**
 *
 * @author rui.joaquim
 */
public interface VerifiedClientRequest<T extends Serializable> 
                                                extends ClientRequest<T>{
    /** 
     * A VerifiedClientRequest provides the access to the cryptographic request
     * hash value used to verify the authenticity of the request. This value 
     * can be used to securely identify the client request.
     *  
     * @return hash value of the client request.
     */
    public byte[] getCryptographicHash();
}
