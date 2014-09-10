/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica;

/**
 * The CommunicationException should be used when any exceptional situation 
 * occurs when invoking methods of the communication layer.
 * 
 * @author rui.joaquim
 */
public class CommunicationException extends Exception{
    
    public CommunicationException (String message){
        super(message);
    }
    
}
