/*
 * Copyright (c) 2014 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.chronological;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ConfigurationManager;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.PostComponent;
import ch.bfh.uniboard.service.PostService;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class ChronologicalService extends PostComponent implements PostService {

	private static final String ATTRIBUTE_NAME = "timestamp";

	@EJB
	PostService postSuccessor;

	@EJB
	ConfigurationManager configurationManager;

	@Override
	protected PostService getPostSuccessor() {
		return this.postSuccessor;
	}

	@Override
	protected Attributes beforePost(byte[] message, Attributes alpha, Attributes beta) {
		long time = new Date().getTime();
		time = 1000 * (time / 1000);
		beta.add(ATTRIBUTE_NAME, new DateValue(new Date(time)));
		return beta;
	}

}
