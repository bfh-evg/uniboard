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
@XmlType(name="Candidate")
public class Candidate extends Choice {
    
    private String lastname;
    private String firstname;
    private String candidateNumber;
    private List<LocalizedText> description;

    public Candidate() {
    }

    public Candidate(int choiceId, String lastname, String firstname, String candidateNumber, List<LocalizedText> description) {
	super(choiceId);
	this.lastname = lastname;
	this.firstname = firstname;
	this.candidateNumber = candidateNumber;
	this.description = description;
    }

    public String getLastname() {
	return lastname;
    }

    public void setLastname(String lastname) {
	this.lastname = lastname;
    }

    public String getFirstname() {
	return firstname;
    }

    public void setFirstname(String firstname) {
	this.firstname = firstname;
    }

    public String getCandidateNumber() {
	return candidateNumber;
    }

    public void setCandidateNumber(String candidateNumber) {
	this.candidateNumber = candidateNumber;
    }

    public List<LocalizedText> getDescription() {
	return description;
    }

    public void setDescription(List<LocalizedText> description) {
	this.description = description;
    }

    
    
}