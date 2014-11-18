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
@XmlSeeAlso({DLEncryptionSetting.class})
public abstract class EncryptionSetting {
    
    protected String encryptionKey;
    protected String type;
    
    public EncryptionSetting() {
    }

    public EncryptionSetting(String encryptionKey, String type) {
	this.encryptionKey = encryptionKey;
	this.type = type;
    }

    public String getEncryptionKey() {
	return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
	this.encryptionKey = encryptionKey;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    
}
