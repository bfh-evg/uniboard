/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.util.interfaces;

/**
 * Defines an identifiable identity in the scope of the BFT protocol. 
 * An identifiable entity has an integer identifier.
 * 
 * @author rui.joaquim
 */
public interface Identity {
    
    /**
     * @return the identifier of the object.
     */
    public int getID();
}
