/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;


import java.io.Serializable;
import lu.uni.bft.client.ClientRequestImpl;
import lu.uni.bft.interfaces.client.ClientRequest;
import lu.uni.bft.interfaces.replica.ClientRequestWithAgreement;

/**
 *
 * @author rui.joaquim
 * @param <T> the type of the class that represents the requested operations.
 */
public class ClientRequestWithAgreementImpl <T extends Serializable>
         extends ClientRequestImpl<T> implements ClientRequestWithAgreement<T>{

    /** The view number in which the agreement was achieved. */
    protected final long viewNumber;
    
    /** The serial number assigned to the request in the view. */
    protected final long viewRequestSN;
   
    /** The signature by the replicas that confirm the agreement. */
    protected final byte[] agreementSignature;
    
    /**
     * @param request The agreed client request.
     * @param viewNumber The view in which the request was agreed.
     * @param viewRequestSN The agreed request SN in the view.
     * @param agreementsignature The agreement signature.
     */
    public ClientRequestWithAgreementImpl(ClientRequest<T> request, 
            long viewNumber, long viewRequestSN, byte[] agreementsignature) {
        
        super(request.getClientID(), request.getClientRequestSN(),
                request.getOperation(), request.getSignature());
        
        this.viewNumber = viewNumber;
        this.viewRequestSN = viewRequestSN;
        this.agreementSignature = agreementsignature;
    }

    @Override
    public byte[] getAgreementSignature() {
        return this.agreementSignature;
    }

    @Override
    public long getViewNumber() {
        return this.viewNumber;
    }

    @Override
    public long getViewRequestSN() {
        return this.viewRequestSN;
    }

   

    
}
