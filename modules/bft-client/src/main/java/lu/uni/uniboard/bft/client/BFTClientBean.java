/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.uniboard.bft.client;

import javax.ejb.Local;
import javax.ejb.Stateless;
import lu.uni.uniboard.bft.service.BFTClient;
import lu.uni.uniboard.bft.service.BFTServiceMessage;

/**
 * This class is the entry point for BFT protocol messages at the BFT 
 * client side.
 * 
 * @author Rui Joaquim
 */
@Stateless
@Local(value = BFTClient.class)
public class BFTClientBean implements BFTClient{

    /**
     * This method receives a BFT protocol message to be processed by the
     * BFT client
     * 
     * @param bftMessage the message to process 
     */
    @Override
    public void processMessage(BFTServiceMessage bftMessage) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
