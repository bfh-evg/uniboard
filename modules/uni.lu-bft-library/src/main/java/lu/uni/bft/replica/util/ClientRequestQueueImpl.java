/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import lu.uni.bft.interfaces.client.ClientRequest;
import lu.uni.bft.replica.util.interfaces.IdentifiableClientRequestQueue;


/**
 * The ClientRequestQueueImpl class provides a thread safe shared queue of 
 client requests. It implements filtering based on the request ID.
 * 
 * @author rui.joaquim@uni.lu
 */
public class ClientRequestQueueImpl implements IdentifiableClientRequestQueue {
    /** The queue that holds the client request */
    private final Queue<ClientRequest> requestQueue;
    
    /** The expected serial number of the next client request to be added 
     * to the queue.
     * Given that the field is of type long it is defined as volatile to
     * ensure atomic reads and writes.
     */
    private volatile long nextRequestSN; 
    
    /** Semaphore to control the access to the client request queue */
    private final Semaphore bufferSemaphore;
    
    /** The capacity of the client request queue 
     * (defined in the object creation). 
     */
    private final int capacity;
    
    /** The ID of the client that issues the messages in this queue. */ 
    private final int clientId;
    
    /**
     * Constructor method.
     * 
     * @param id the identifier of client corresponding to this queue.
     * @param size defines how many client requests can be stored in the queue.
     */
    public ClientRequestQueueImpl(int id, int size){
        this.requestQueue = new ArrayDeque<>(size);
        this.nextRequestSN = 0;
        this.bufferSemaphore = new Semaphore(size);
        this.capacity = size;
        this.clientId = id;
    }
    
    @Override
    public long getNextMsgSN(){
        return this.nextRequestSN;
    }
    
    @Override
    public long addRequest(ClientRequest request) throws InterruptedException{
        //test the request is null and if it has the expected request SN
        
        //create a local copy of the nextRequest to avoid synchronization issues
        long nextSN = this.nextRequestSN; 
        if(request == null || nextSN != request.getClientRequestSN()) {
            //request out of order
            return nextSN;
        } else {
            //valid request order
            //make sure that there is space in the queue
            this.bufferSemaphore.acquire();
            //add request to the queue
            return add(request);
        }
    }
     
    /**
     * Adds request to the queue if the request serial number (SN) match the 
     * expected request SN, otherwise it just discards the request.
     * @param request to add to the queue
     * @return the next expected request SN
     */
    private synchronized long add(ClientRequest request){
        if(this.nextRequestSN != request.getClientRequestSN()){
            //invalid request: discard request and release "its semaphore permit"
            this.bufferSemaphore.release();
        } else {
            //add request and increment the next expected request SN
            this.requestQueue.offer(request);
            this.nextRequestSN++;
        }
        return this.nextRequestSN;
    }
    
    
    @Override
    public synchronized ClientRequest getNextRequest(){
        if(this.requestQueue.isEmpty()){
            return null;
        } else {
            //request removed, thus releare one semaphore permit
            this.bufferSemaphore.release();
            return this.requestQueue.poll();          
        }
    } 
    
    @Override
    public synchronized void clearRequest(long clearRequestSN){
        if(clearRequestSN >= this.nextRequestSN){
            //remove all elements
            this.requestQueue.clear();
            //give the corresponding permits back to the semaphore
            int availablePermits = this.bufferSemaphore.availablePermits();
            this.bufferSemaphore.release(this.capacity - availablePermits);
            //update next expected request SN
            this.nextRequestSN = clearRequestSN + 1;
                        
        } else {
            ClientRequest req = this.requestQueue.peek();
            // the addRequest method ensures that there are no null elements 
            // in the queue, thus null is returned only when the queue is empty.
            while(req != null) {
                if(req.getClientRequestSN() <= clearRequestSN){
                    //remove request and add one permit to the semaphore
                    this.requestQueue.poll();
                    this.bufferSemaphore.release();
                    //peek next request in the queue
                    req = this.requestQueue.peek();
                    
                } else { //no more requests to clean
                    break;
                }
            }
        }        
    }

    /**
     * 
     * @return the ID of the client whose requests are added to this queue 
     */
    @Override
    public int getID() {
        return this.clientId;
    }
    
}
