/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election.crypto;

import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author phil
 */
@XmlType(name="")
public class DLSignatureSetting extends SignatureSetting{
    
    private String p;
    private String q;

    public DLSignatureSetting() {
    }

    public DLSignatureSetting(String ghat, String p, String q) {
	super(ghat, "DL");
	this.p = p;
	this.q = q;
    }

    public String getP() {
	return p;
    }

    public void setP(String p) {
	this.p = p;
    }

    public String getQ() {
	return q;
    }

    public void setQ(String q) {
	this.q = q;
    }
    
    
	    
}
