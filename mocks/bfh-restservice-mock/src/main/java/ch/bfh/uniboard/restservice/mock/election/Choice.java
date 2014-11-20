/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author phil
 */
@XmlSeeAlso({Candidate.class, Option.class, PartyCandidate.class, Party.class})
public class Choice {
    
    protected int choiceId;
    protected final String objectType;

    public Choice() {
	this.objectType = this.getClass().getSimpleName();
    }


    public Choice(int choiceId) {
	this.objectType = this.getClass().getSimpleName();//.getCanonicalName();
	this.choiceId = choiceId;
    }

    public int getChoiceId() {
	return choiceId;
    }
    

    public void setChoiceId(int choiceId) {
	this.choiceId = choiceId;
    }
    
    
}
