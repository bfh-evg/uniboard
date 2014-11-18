/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election.crypto;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author phil
 */
@XmlSeeAlso({DLSignatureSetting.class})
public abstract class SignatureSetting {
    
    protected String ghat;
    protected String type;

    public SignatureSetting() {
    }

    public SignatureSetting(String ghat, String type) {
	this.ghat = ghat;
	this.type = type;
    }

    public String getGhat() {
	return ghat;
    }

    public void setGhat(String ghat) {
	this.ghat = ghat;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }
    
    
}
