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
@XmlType(name="Option")
public class Option extends Choice{
    
    private List<LocalizedText> text;

    public Option() {
    }

    public Option(int choiceId, List<LocalizedText> text) {
	super(choiceId);
	this.text = text;
    }

    public List<LocalizedText> getText() {
	return text;
    }

    public void setText(List<LocalizedText> text) {
	this.text = text;
    }
    
    
    
}
