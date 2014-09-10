/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica.util.interfaces;

import lu.uni.bft.interfaces.client.ClientRequest;

/**
 *
 * @author rui.joaquim@uni.lu
 */
public interface ClientRequestQueue {
    /**
     * Method to obtain the next expected message serial number (SN).
     * @return the value of the expected next message SN.
     */
    public long getNextMsgSN();
    
    /**
     * Adds the client request to the queue if the request SN is the next 
     * expected SN and if there is space available. The call to this 
     * method will block if the request SN is equal to the expected SN and 
     * there is no space available in the queue. the request is ignored it 
     * is null.
     * 
     * @param request to be added to the queue.
     * @return the next expected request SN. 
     * @throws java.lang.InterruptedException if the thread is interrupted 
     * while waiting for a free space in the queue.
     */
    public long addRequest(ClientRequest request) throws InterruptedException;
    
     /**
     * Method to obtain the first client request in the queue
     * @return the first request in the queue or null if the queue is empty
     */
    public ClientRequest getNextRequest();
    
     /**
     * This method removes all requests in the queue with SN <= clearRequestSN
     * @param clearRequestSN 
     */
    public void clearRequest(long clearRequestSN);
}
