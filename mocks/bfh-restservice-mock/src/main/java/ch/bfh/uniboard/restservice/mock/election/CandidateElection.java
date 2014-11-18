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
@XmlType(name="CandidateElection")
public class CandidateElection extends Election<Candidate> {

    @XmlElements({
	@XmlElement(type = ChoiceList.class),
    })
    private List<CandidateList> candidateLists;

    public CandidateElection() {
    }

    public CandidateElection(List<LocalizedText> title, List<LocalizedText> description, List<Candidate> choices, List<CandidateList> candidateLists, List<Rule> rules, SignatureSetting signatureSetting, EncryptionSetting encryptionSetting) {
	super(title, description, choices, rules,  signatureSetting, encryptionSetting);
	this.candidateLists = candidateLists;
    }

   

    public List<CandidateList> getCandidateLists() {
	return candidateLists;
    }

    public void setCandidateLists(List<CandidateList> candidateLists) {
	this.candidateLists = candidateLists;
    }
    
       
}
