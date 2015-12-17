/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.uniboard.ordered;

import ch.bfh.uniboard.service.State;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class OrderedState extends State {

	private Map<String, Integer> sections = new HashMap<>();

	public Map<String, Integer> getSections() {
		return sections;
	}

	public void setSections(Map<String, Integer> sections) {
		this.sections = sections;
	}

}
