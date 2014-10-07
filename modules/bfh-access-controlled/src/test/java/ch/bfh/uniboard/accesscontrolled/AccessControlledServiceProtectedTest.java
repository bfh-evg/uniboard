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
package ch.bfh.uniboard.accesscontrolled;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayElement;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZElement;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test the protected methods which don't require arquillian.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class AccessControlledServiceProtectedTest {

	public AccessControlledServiceProtectedTest() {
	}

	@Test
	public void testCreateMessageElement() {

		AccessControlledService service = new AccessControlledService();
		byte[] message = new byte[1];
		message[0] = 0x6;
		String string = "test";
		int integer = 10;
		byte[] byteA = new byte[1];
		byteA[0] = 0x7;
		Date date = new Date();
		Attributes alpha = new Attributes();
		alpha.add("a1", new StringValue(string));
		alpha.add("a2", new IntegerValue(integer));
		alpha.add("a3", new ByteArrayValue(byteA));
		alpha.add("a4", new DateValue(date));
		Element result = service.createMessageElement(message, alpha);
		assertTrue(result instanceof Pair);
		Pair resultPair = (Pair) result;
		ByteArrayElement resultMessage = (ByteArrayElement) resultPair.getFirst();
		assertArrayEquals(resultMessage.getValue().getBytes(), message);

		assertTrue(resultPair.getSecond().isTuple());
		Tuple alphaResult = (Tuple) resultPair.getSecond();
		assertEquals(string, ((StringElement) alphaResult.getAt(0)).getValue());
		assertEquals(integer, ((ZElement) alphaResult.getAt(1)).getValue().intValue());
		assertArrayEquals(byteA, ((ByteArrayElement) alphaResult.getAt(2)).getValue().getBytes());
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(timeZone);
		String expectedDateString = dateFormat.format(date);
		assertEquals(expectedDateString, ((StringElement) alphaResult.getAt(3)).getValue());

	}

}
