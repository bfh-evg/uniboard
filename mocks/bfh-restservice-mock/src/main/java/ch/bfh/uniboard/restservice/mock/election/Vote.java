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
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author phil
 */
@XmlType(name="Vote")
public class Vote extends Election{
    private List<LocalizedText> question;

    public Vote() {
    }

    public Vote(List<LocalizedText> title, List<LocalizedText> description, List<LocalizedText> question, List choices, List rules, SignatureSetting signatureSetting,
	    EncryptionSetting encryptionSetting) {
	super(title, description, choices, rules,  signatureSetting, encryptionSetting);
	this.question = question;
    }

    public List<LocalizedText> getQuestion() {
	return question;
    }

    public void setQuestion(List<LocalizedText> question) {
	this.question = question;
    }
    
}
