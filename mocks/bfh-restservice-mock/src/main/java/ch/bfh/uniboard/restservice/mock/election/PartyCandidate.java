/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import ch.bfh.uniboard.restservice.mock.LocalizedText;
import java.util.List;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author phil
 */
@XmlType(name="PartyCandidate")
public class PartyCandidate extends Candidate {
    
    private int partyId;

    public PartyCandidate() {
    }

    public PartyCandidate(int choiceId, String lastname, String firstname, String candidateNumber, int partyId, List<LocalizedText> description) {
	super(choiceId, lastname, firstname, candidateNumber, description);
	this.partyId = partyId;
    }

    public int getPartyId() {
	return partyId;
    }

    public void setPartyId(int partyId) {
	this.partyId = partyId;
    }

    
    
    
}
