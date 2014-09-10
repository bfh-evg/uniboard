/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica.util;

import java.util.concurrent.ArrayBlockingQueue;
import lu.uni.bft.replica.util.interfaces.IdentifiableQueue;


/**
 * IdentifiableQueueImpl extends the ArrayBlockingQueue class to implement
 * the IdentifiableQueue interface.
 * 
 * @author rui.joaquim
 * 
 * @param <T> the type of elements that are accepted by the queue.
 */
public class IdentifiableQueueImpl<T> extends ArrayBlockingQueue<T> 
                                 implements IdentifiableQueue<T>{

    private final int id;
    
    /**
     * Creates an Identifiable queue with the capacity and access policy as
     * defined by the underlying class ArrayBlockingQueue.
     * 
     * @param id the queue Id.
     * @param capacity the (fixed) capacity of this queue.
     * @param fair access policy for the queue.
     */
    public IdentifiableQueueImpl(int id, int capacity, boolean fair) {
        super(capacity, fair);
        this.id = id;
    }

    @Override
    public int getID() {
        return this.id;
    }
    
}
