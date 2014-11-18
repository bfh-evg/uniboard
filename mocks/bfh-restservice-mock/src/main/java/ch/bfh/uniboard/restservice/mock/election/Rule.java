/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import java.util.List;
import javax.xml.bind.annotation.XmlSeeAlso;

/**
 *
 * @author phil
 */
@XmlSeeAlso({ForAllRule.class, SummationRule.class})
public abstract class Rule {
    
    protected int lowerBound;
    protected int upperBound;
    protected List<Integer> choiceIds;
    protected final String objectType;

    public Rule() {
	this.objectType = this.getClass().getSimpleName();
    }


    public Rule(int lowerBound, int upperBound, List<Integer> choiceIds) {
	this.objectType = this.getClass().getSimpleName();
	this.lowerBound = lowerBound;
	this.upperBound = upperBound;
	this.choiceIds = choiceIds;
    }

    public int getLowerBound() {
	return lowerBound;
    }

    public void setLowerBound(int lowerBound) {
	this.lowerBound = lowerBound;
    }

    public int getUpperBound() {
	return upperBound;
    }

    public void setUpperBound(int upperBound) {
	this.upperBound = upperBound;
    }

    public List<Integer> getChoiceIds() {
	return choiceIds;
    }

    public void setChoiceIds(List<Integer> choiceIds) {
	this.choiceIds = choiceIds;
    }

    public String getObjectType() {
	return objectType;
    }
    
    
    
}
