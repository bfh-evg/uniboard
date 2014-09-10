/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;


import lu.uni.bft.interfaces.replica.PrePrepareMessage;
import lu.uni.bft.interfaces.client.ClientRequest;

/**
 *
 * @author rui.joaquim
 */
public class PrePrepareMessageImpl extends BFTProtocolMessageImpl implements PrePrepareMessage{

    public static final long serialVersionUID = 1L;
    
    /** The client request  to be agreed on. */
    protected ClientRequest request;
    
    
    /**
     * 
     * @param view the view number.
     * @param requestSN the request serial number in the view.
     * @param replica the replica id.
     * @param request the client request.
     */
    public PrePrepareMessageImpl(long view, long requestSN, int replica, ClientRequest request) {
        super(view, requestSN, replica);
        
        this.request = request;
    }

    
    @Override
    public ClientRequest getRequest() {
       return this.request;
    }
    
    
}
