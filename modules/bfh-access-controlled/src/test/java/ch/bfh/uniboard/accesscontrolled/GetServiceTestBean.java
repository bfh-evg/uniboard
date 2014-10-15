/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.accesscontrolled;

import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@LocalBean
public class GetServiceTestBean implements GetService {

	private List<ResultContainer> feedback = new ArrayList<>();
	private Query input;

	@Override
	public ResultContainer get(Query query) {
		this.input = query;
		ResultContainer result = this.feedback.remove(0);
		return result;
	}

	public Query getInput() {
		return input;
	}

	public void addFeedback(ResultContainer feedback) {
		this.feedback.add(feedback);
	}
}
