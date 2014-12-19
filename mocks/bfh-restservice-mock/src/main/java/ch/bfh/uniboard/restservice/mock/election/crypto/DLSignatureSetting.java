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
@XmlType(name = "")
public class DLSignatureSetting extends SignatureSetting {

    private String p;
    private String q;
    private String g;

    public DLSignatureSetting() {
    }

    public DLSignatureSetting(String ghat, String p, String q, String g) {
	super(ghat, "DL");
	this.p = p;
	this.q = q;
	this.g = g;
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

    public String getG() {
	return g;
    }

    public void setG(String g) {
	this.g = g;
    }
}
