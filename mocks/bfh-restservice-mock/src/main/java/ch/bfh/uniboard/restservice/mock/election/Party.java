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
@XmlType(name="Party")
public class Party extends Choice {
    
    private List<LocalizedText> name;

    public Party() {
    }

    public Party(int choiceId, List<LocalizedText> name) {
	super(choiceId);
	this.name = name;
    }

    public List<LocalizedText> getName() {
	return name;
    }

    public void setName(List<LocalizedText> name) {
	this.name = name;
    }

}
