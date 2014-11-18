/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author phil
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MultiElection {
    
    private List<Election> elections;

    public MultiElection() {
    }

    public MultiElection(List<Election> elections) {
	this.elections = elections;
    }

    public List<Election> getElections() {
	return elections;
    }
    
    
}
