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
import ch.bfh.uniboard.service.GetComponent;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.ResultContainer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
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
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		dateFormat.setTimeZone(timeZone);
		String dateTime = dateFormat.format(new Date());
		gamma.add(new Attribute(ATTRIBUTE_NAME, dateTime, DataType.DATE));
		return gamma;
	}

}
