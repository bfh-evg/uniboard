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
package ch.bfh.uniboard;

import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@LocalBean
public class GetServiceTestBean implements GetService {

	private ResultContainer feedback;
	private Query input;

	@Override
	public ResultContainer get(Query query) {
		this.input = query;
		return this.feedback;
	}

	public Query getInput() {
		return input;
	}

	public void setFeedback(ResultContainer feedback) {
		this.feedback = feedback;
	}
}
