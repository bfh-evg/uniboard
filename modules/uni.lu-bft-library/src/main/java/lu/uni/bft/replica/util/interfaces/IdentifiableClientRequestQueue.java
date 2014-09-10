/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.bft.replica.util.interfaces;

import lu.uni.bft.util.interfaces.Identity;

/**
 * Utility interface to join the ClientRequestQueue and Identity interfaces.
 * 
 * @author rui.joaquim
 */
public interface IdentifiableClientRequestQueue extends ClientRequestQueue, 
            Identity{
    
}
