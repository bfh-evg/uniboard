/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import ch.bfh.uniboard.restservice.mock.LocalizedText;
import java.util.List;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author phil
 */
@XmlSeeAlso({CandidateList.class, PartyList.class})
public abstract class ChoiceList {
    
    protected final String objectType;

    protected List<LocalizedText> name;
    protected List<LocalizedText> listNumber;
    protected List<Integer> choicesIds;

    public ChoiceList() {
	this.objectType = this.getClass().getSimpleName();
    }

    public ChoiceList(List<LocalizedText> name, List<LocalizedText> listNumber, List<Integer> choicesIds) {
	this.name = name;
	this.listNumber = listNumber;
	this.choicesIds = choicesIds;
	this.objectType = this.getClass().getSimpleName();
    }

    public List<LocalizedText> getName() {
	return name;
    }

    public void setName(List<LocalizedText> name) {
	this.name = name;
    }

    public List<LocalizedText> getListNumber() {
	return listNumber;
    }

    public void setListNumber(List<LocalizedText> listNumber) {
	this.listNumber = listNumber;
    }

    public List<Integer> getChoicesIds() {
	return choicesIds;
    }

    public void setChoicesIds(List<Integer> choicesIds) {
	this.choicesIds = choicesIds;
    }
    
    public String getObjectType() {
	return objectType;
    }
    
}
