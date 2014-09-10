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
 * @author rui.joaquim@uni.lu
 */
public interface ClientRequestWithAgreement<T extends Serializable> 
                                                extends ClientRequest<T>{
    /**
     * OPTIONAL
     * @return a threshold distributed signature on the request COMMIT 
     *         agreement messages.
     */
    public byte[] getAgreementSignature();
    
    /** @return the view number in which the request was agreed. */
    public long getViewNumber();
    
    /** @return the view request serial number agreed for this request. */
    public long getViewRequestSN();
   
}
