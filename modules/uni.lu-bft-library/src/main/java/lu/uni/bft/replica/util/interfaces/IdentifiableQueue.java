/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica.util.interfaces;

import java.util.Queue;
import lu.uni.bft.util.interfaces.Identity;

/**
 * Utility interface to join the Queue and Identity interfaces.
 * 
 * @author rui.joaquim
 * 
 * @param <T> The type of elements accepted by the Queue interface.
 */
public interface IdentifiableQueue<T> extends Queue<T>, Identity{
    
}
