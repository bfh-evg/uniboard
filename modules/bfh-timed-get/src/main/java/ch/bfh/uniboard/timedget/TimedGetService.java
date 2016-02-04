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
package ch.bfh.uniboard.timedget;

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.GetComponent;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.ResultContainer;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class TimedGetService extends GetComponent implements GetService {

	private static final String ATTRIBUTE_NAME = "timestamp";

	@EJB
	GetService getSuccessor;

	@Override
	protected GetService getGetSuccessor() {
		return this.getSuccessor;
	}

	@Override
	protected Attributes afterGet(Query query, ResultContainer resultContainer) {
		Attributes gamma = resultContainer.getGamma();
		long time = new Date().getTime();
		time = 1000 * (time / 1000);
		gamma.add(ATTRIBUTE_NAME, new DateValue(new Date(time)));
		return gamma;
	}

}
