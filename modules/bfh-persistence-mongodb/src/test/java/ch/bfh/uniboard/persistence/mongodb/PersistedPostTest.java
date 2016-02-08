/*
 * Uniboard
 *
 *  Copyright (c) 2015 Bern University of Applied Sciences (BFH),
 *  Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniVote2 may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH),
 *   Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: severin.hauser@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.uniboard.persistence.mongodb;

import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.DataType;
import com.mongodb.util.JSON;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class PersistedPostTest {

	private static byte[] message;
	private static Attributes alpha;
	private static Attributes beta;
	private static PersistedPost pp;

	public PersistedPostTest() {
	}

	/**
	 * Prepare a post that will be used in the tests
	 */
	@BeforeClass
	public static void setUpClass() {
		message = new byte[]{1, 2, 3, 4};

		alpha = new Attributes();
		alpha.add(new Attribute("first", "value1"));
		alpha.add(new Attribute("second", "2", DataType.INTEGER));
		alpha.add(new Attribute("third", "0xNDEwMjEwOTM5MDZaFw0xNjEwMjEwnO"));
		alpha.add(new Attribute("fourth", "2014-10-15T13:00:00Z", DataType.DATE));

		beta = new Attributes();
		beta.add(new Attribute("fifth", "value5"));
		beta.add(new Attribute("seventh", "7", DataType.INTEGER));
		beta.add(new Attribute("eighth", "0xNDEwMjEwOTM5MDZaFw0xNjEwMjEwnO"));

		pp = new PersistedPost(message, alpha, beta);
	}

	@AfterClass
	public static void tearDownClass() {

	}

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test if the PersistedPost was created correctly
	 */
	@Test
	public void createPersistedPostTest() {

		assertEquals(message[0], pp.getMessage()[0]);
		assertEquals(message[1], pp.getMessage()[1]);
		assertEquals(message[2], pp.getMessage()[2]);
		assertEquals(message[3], pp.getMessage()[3]);

		assertEquals(alpha.getAttribute("first"), pp.getAlpha().getAttribute("first"));
		assertEquals(alpha.getAttribute("second"), pp.getAlpha().getAttribute("second"));
		assertEquals(alpha.getAttribute("third"), pp.getAlpha().getAttribute("third"));
		assertEquals(alpha.getAttribute("fourth"), pp.getAlpha().getAttribute("fourth"));
		assertEquals(null, pp.getAlpha().getAttribute("fifth"));

		assertEquals(beta.getAttribute("fifth"), pp.getBeta().getAttribute("fifth"));
		assertEquals(beta.getAttribute("sixth"), pp.getBeta().getAttribute("sixth"));
		assertEquals(beta.getAttribute("seventh"), pp.getBeta().getAttribute("seventh"));
		assertEquals(beta.getAttribute("eighth"), pp.getBeta().getAttribute("eighth"));
		assertEquals(null, pp.getBeta().getAttribute("first"));

	}

	/**
	 * Test the conversion from a PersistedPost to a Document
	 */
	@Test
	public void toDocumentTest() throws ParseException {
		Document dbObj = pp.toDocument();

		assertTrue(dbObj.containsKey("message"));
		assertTrue(dbObj.containsKey("alpha"));
		assertTrue(dbObj.containsKey("beta"));

		assertEquals(message[0], pp.getMessage()[0]);
		assertEquals(message[1], pp.getMessage()[1]);
		assertEquals(message[2], pp.getMessage()[2]);
		assertEquals(message[3], pp.getMessage()[3]);

		assertTrue(((Document) dbObj.get("alpha")).containsKey("first"));
		assertTrue(((Document) dbObj.get("alpha")).containsKey("second"));
		assertTrue(((Document) dbObj.get("alpha")).containsKey("third"));
		assertTrue(((Document) dbObj.get("alpha")).containsKey("fourth"));

		assertEquals(alpha.getAttribute("first").getValue(), ((Document) dbObj.get("alpha")).get("first"));
		Integer int2 = Integer.parseInt(alpha.getAttribute("second").getValue());
		assertEquals(int2, ((Document) dbObj.get("alpha")).get("second"));
		assertEquals(alpha.getAttribute("third").getValue(), ((Document) dbObj.get("alpha")).get("third"));
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		dateFormat.setTimeZone(timeZone);
		Date d4 = dateFormat.parse(alpha.getAttribute("fourth").getValue());
		assertEquals(d4, ((Document) dbObj.get("alpha")).get("fourth"));

		assertTrue(((Document) dbObj.get("beta")).containsKey("fifth"));
		assertTrue(((Document) dbObj.get("beta")).containsKey("seventh"));
		assertTrue(((Document) dbObj.get("beta")).containsKey("eighth"));

		assertEquals(beta.getAttribute("fifth").getValue(), ((Document) dbObj.get("beta")).get("fifth"));
		Integer int7 = Integer.parseInt(beta.getAttribute("seventh").getValue());
		assertEquals(int7, ((Document) dbObj.get("beta")).get("seventh"));
		assertEquals(beta.getAttribute("eighth").getValue(), ((Document) dbObj.get("beta")).get("eighth"));

	}

	/**
	 * Test the conversion from Document to PersistedPost
	 */
	@Test
	public void fromDocumentTest() throws ParseException {
		Document doc = pp.toDocument();

		PersistedPost newPP = PersistedPost.fromDocument(doc);

		assertEquals(message[0], newPP.getMessage()[0]);
		assertEquals(message[1], newPP.getMessage()[1]);
		assertEquals(message[2], newPP.getMessage()[2]);
		assertEquals(message[3], newPP.getMessage()[3]);

		assertEquals(alpha.getAttribute("first"), newPP.getAlpha().getAttribute("first"));
		assertEquals(alpha.getAttribute("second"), newPP.getAlpha().getAttribute("second"));
		assertEquals(alpha.getAttribute("third"), newPP.getAlpha().getAttribute("third"));
		assertEquals(alpha.getAttribute("fourth"), newPP.getAlpha().getAttribute("fourth"));

		assertEquals(beta.getAttribute("fifth"), newPP.getBeta().getAttribute("fifth"));
		assertEquals(beta.getAttribute("seventh"), newPP.getBeta().getAttribute("seventh"));
		assertEquals(beta.getAttribute("eighth"), newPP.getBeta().getAttribute("eighth"));
	}

	/**
	 * Test the use of a JSON string as Message
	 *
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void jsonMessageTest() throws UnsupportedEncodingException, ParseException {
		String jsonMessage = "{ \"alpha\" : [ { \"first\" : \"value1\"} , { \"second\" : \"value2\"}] , \"beta\" : [ { \"fifth\" : \"value5\"} , { \"sixth\" : \"value6\"}]}";

		PersistedPost pp2 = new PersistedPost(jsonMessage.getBytes("UTF-8"), alpha, beta);

		Document dbObj = pp2.toDocument();

		assertEquals(jsonMessage, JSON.serialize(dbObj.get("searchable-message")));

		PersistedPost pp3 = PersistedPost.fromDocument(dbObj);

		assertEquals(jsonMessage, new String(pp3.getMessage(), "UTF-8"));

	}

}
