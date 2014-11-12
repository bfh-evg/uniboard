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

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.Value;
import java.util.Date;
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
	public void testbeforePost() throws InterruptedException {
		TimedGetService service = new TimedGetService();
		Query q = new Query(null);
		Attributes gamma = new Attributes();
		ResultContainer rc = new ResultContainer(null, gamma);
		Date before = new Date(1000 * (new Date().getTime() / 1000));
		gamma = service.afterGet(q, rc);
		Date after = new Date(1000 * (new Date().getTime() / 1000));

		Value tmp = gamma.getValue("timestamp");
		if (!(tmp instanceof DateValue)) {
			fail();
		}
		DateValue date = (DateValue) tmp;
		assertTrue(before.before(date.getValue()) || before.equals(date.getValue()));
		assertTrue(after.after(date.getValue()) || after.equals(date.getValue()));
	}
	
}
