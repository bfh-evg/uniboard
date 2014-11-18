/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.restservice.mock.election;

import java.util.List;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author phil
 */
@XmlType(name="ForAllRule")
public class ForAllRule extends Rule {

    public ForAllRule() {
    }

    public ForAllRule(int lowerBound, int upperBound, List<Integer> choiceId) {
	super(lowerBound, upperBound, choiceId);
    }
    
}
