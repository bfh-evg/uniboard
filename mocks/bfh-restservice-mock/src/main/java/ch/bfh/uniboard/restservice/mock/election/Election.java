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
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author phil
 */
@XmlSeeAlso({CandidateElection.class, PartyElection.class, Vote.class})
public abstract class Election<V extends Choice> {
    
    protected final String objectType;
    
    @XmlMixed
    @XmlElements({
	@XmlElement(type = Choice.class)
    })
    protected List<V> choices;
    protected List<Rule> rules;
    protected List<LocalizedText> title;
    protected List<LocalizedText> description;
    protected SignatureSetting signatureSetting;
    protected EncryptionSetting encryptionSetting;

    public Election() {
	this.objectType = this.getClass().getSimpleName();
    }

    public Election(List<LocalizedText> title, List<LocalizedText> description, List<V> choices, List<Rule> rules, SignatureSetting signatureSetting, EncryptionSetting encryptionSetting) {
	this.objectType = this.getClass().getSimpleName();
	this.choices = choices;
	this.rules = rules;
	this.title = title;
	this.description = description;
	this.signatureSetting = signatureSetting;
	this.encryptionSetting = encryptionSetting;
    }

    
    public List<V> getChoices() {
	return choices;
    }

    public void setChoices(List<V> choices) {
	this.choices = choices;
    }

    public List<Rule> getRules() {
	return rules;
    }

    public void setRules(List<Rule> rules) {
	this.rules = rules;
    }
    
    public String getObjectType() {
	return objectType;
    }

    public List<LocalizedText> getTitle() {
	return title;
    }

    public void setTitle(List<LocalizedText> title) {
	this.title = title;
    }

    public List<LocalizedText> getDescription() {
	return description;
    }

    public void setDescription(List<LocalizedText> description) {
	this.description = description;
    }

    public SignatureSetting getSignatureSetting() {
	return signatureSetting;
    }

    public void setSignatureSetting(SignatureSetting signatureSetting) {
	this.signatureSetting = signatureSetting;
    }

    public EncryptionSetting getEncryptionSetting() {
	return encryptionSetting;
    }

    public void setEncryptionSetting(EncryptionSetting encryptionSetting) {
	this.encryptionSetting = encryptionSetting;
    }
    
    
    
}
