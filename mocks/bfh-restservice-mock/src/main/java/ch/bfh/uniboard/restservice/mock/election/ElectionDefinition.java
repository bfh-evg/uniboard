/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import ch.bfh.uniboard.restservice.mock.LocalizedText;
import java.util.Date;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 *
 * @author phil
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ElectionDefinition {
    
    private List<LocalizedText> title;
    private List<LocalizedText> description;
    private Date votingPeriodBegin;
    private Date votingPeriodEnd;

    public ElectionDefinition() {
    }

    public ElectionDefinition(List<LocalizedText> title, List<LocalizedText> description, Date votingPeriodBegin,
	    Date votingPeriodEnd) {
	this.title = title;
	this.description = description;
	this.votingPeriodBegin = votingPeriodBegin;
	this.votingPeriodEnd = votingPeriodEnd;
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

    public Date getVotingPeriodBegin() {
	return votingPeriodBegin;
    }

    public void setVotingPeriodBegin(Date votingPeriodBegin) {
	this.votingPeriodBegin = votingPeriodBegin;
    }

    public Date getVotingPeriodEnd() {
	return votingPeriodEnd;
    }

    public void setVotingPeriodEnd(Date votingPeriodEnd) {
	this.votingPeriodEnd = votingPeriodEnd;
    }


    
    
    
    
}
