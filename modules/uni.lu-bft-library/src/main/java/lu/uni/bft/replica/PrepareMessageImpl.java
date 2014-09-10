/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;


import lu.uni.bft.interfaces.replica.PrepareMessage;


/**
 *
 * @author rui.joaquim
 */
public class PrepareMessageImpl extends BFTProtocolMessageImpl implements PrepareMessage{

    public static final long serialVersionUID = 1L;
    
    /** The hash (digest) of the client request. */
    protected byte[] requestHash;
    
    /**
     * 
     * @param view the view number.
     * @param requestSN the request serial number in the view.
     * @param replica the replica id.
     * @param requestHash the request digest (hash) value.
     */
    public PrepareMessageImpl(long view, long requestSN, int replica, byte[] requestHash) {
        super(view, requestSN, replica);
        
        this.requestHash = requestHash;
    }

    
    @Override
    public byte[] getRequestHash() {
        return this.requestHash;
    }
    
}
