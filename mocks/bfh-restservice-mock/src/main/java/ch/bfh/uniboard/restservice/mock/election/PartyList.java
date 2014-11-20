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
@XmlType(name="PartyList")
public class PartyList extends ChoiceList {
    
    private int partyId;

    public PartyList() {
    }

    public PartyList(List<LocalizedText> name, List<LocalizedText> shortName, int partyId, List<Integer> choiceIds) {
	super(name, shortName, choiceIds);
	this.partyId = partyId;
    }

    public int getPartyId() {
	return partyId;
    }

    public void setPartyId(int partyId) {
	this.partyId = partyId;
    }
    
    
}
