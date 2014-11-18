/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import ch.bfh.uniboard.restservice.mock.LocalizedText;
import ch.bfh.uniboard.restservice.mock.election.crypto.EncryptionSetting;
import ch.bfh.uniboard.restservice.mock.election.crypto.SignatureSetting;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author phil
 */
@XmlType(name="PartyElection")
public class PartyElection extends Election<Choice>{

    @XmlElements({
	@XmlElement(type = ChoiceList.class),
    })
    private List<PartyList> partyLists;

    public PartyElection() {
    }

    public PartyElection(List<LocalizedText> title, List<LocalizedText> description, List<Choice> choices, List<PartyList> partyLists, List<Rule> rules, SignatureSetting signatureSetting, EncryptionSetting encryptionSetting) {
	super(title, description, choices, rules,  signatureSetting, encryptionSetting);
	this.partyLists = partyLists;
    }

    
    public List<PartyList> getPartyLists() {
	return partyLists;
    }

    public void setPartyLists(List<PartyList> partyLists) {
	this.partyLists = partyLists;
    }
    
    
    
}
