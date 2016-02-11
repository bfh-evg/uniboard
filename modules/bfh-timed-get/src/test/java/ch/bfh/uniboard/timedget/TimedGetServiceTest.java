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

import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.ResultContainer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class TimedGetServiceTest {

	public TimedGetServiceTest() {
	}

	@Test
	public void testbeforePost() throws InterruptedException, ParseException {
		TimedGetService service = new TimedGetService();
		Query q = new Query(null);
		Attributes gamma = new Attributes();
		ResultContainer rc = new ResultContainer(null, gamma);
		Date before = new Date(1000 * (new Date().getTime() / 1000));
		gamma = service.afterGet(q, rc);
		Date after = new Date(1000 * (new Date().getTime() / 1000));

		Attribute tmp = gamma.getAttribute("timestamp");
		if (tmp.getDataType() != DataType.DATE) {
			fail();
		}
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		dateFormat.setTimeZone(timeZone);
		Date date = dateFormat.parse(tmp.getValue());
		assertTrue(before.before(date) || before.equals(date));
		assertTrue(after.after(date) || after.equals(date));
	}

}
