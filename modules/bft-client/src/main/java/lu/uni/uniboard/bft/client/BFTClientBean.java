/*
 * Copyright (c) 2014 ... Rui?
 *
 * Project VIVO.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package lu.uni.uniboard.bft.client;

import javax.ejb.Local;
import javax.ejb.Stateless;
import lu.uni.uniboard.bft.service.BFTClient;
import lu.uni.uniboard.bft.service.BFTServiceMessage;

/**
 * This class is the entry point for BFT protocol messages at the BFT
 * client side.
 * <p>
 * TODO: Envision to inherit from "ch.bfh.uniboard.service.Component".
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
