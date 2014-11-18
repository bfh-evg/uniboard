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
@XmlType(name="CandidateList")
public class CandidateList extends ChoiceList {

    public CandidateList() {
    }

    public CandidateList(List<LocalizedText> name, List<LocalizedText> shortName, List<Integer> choiceIds) {
	super(name, shortName, choiceIds);
    }
    
}
