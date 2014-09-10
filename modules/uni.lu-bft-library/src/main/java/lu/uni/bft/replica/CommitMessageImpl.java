/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;

import lu.uni.bft.interfaces.replica.CommitMessage;

/**
 *
 * @author rui.joaquim
 */
public class CommitMessageImpl extends BFTProtocolMessageImpl implements CommitMessage {

    public static final long serialVersionUID = 1L;
    
    /**
     * 
     * @param view the view number.
     * @param requestSN the request serial number in the view.
     * @param replica the replica ID.
     */
    public CommitMessageImpl(long view, long requestSN, int replica) {
        super(view, requestSN, replica);
    }
    
}
