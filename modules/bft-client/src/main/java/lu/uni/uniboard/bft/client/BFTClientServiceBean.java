/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package lu.uni.uniboard.bft.client;

import javax.ejb.Local;
import javax.ejb.Stateless;
import lu.uni.uniboard.bft.service.BFTClientService;
import lu.uni.uniboard.bft.service.BFTServiceMessage;

/**
 *
 * @author rui.joaquim
 */
@Stateless
@Local(value = BFTClientService.class)
public class BFTClientServiceBean implements BFTClientService{

    @Override
    public void processMessage(BFTServiceMessage bftMessage) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
