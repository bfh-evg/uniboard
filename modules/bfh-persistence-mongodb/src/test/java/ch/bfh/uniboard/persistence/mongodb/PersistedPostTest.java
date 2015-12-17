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

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import com.mongodb.util.JSON;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import org.bson.Document;
import org.bson.types.Binary;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
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
		alpha.add("first", new StringValue("value1"));
		alpha.add("second", new IntegerValue(2));
		alpha.add("third", new ByteArrayValue(new byte[]{3, 3}));
		alpha.add("fourth", new DateValue(new Date(System.currentTimeMillis())));

		beta = new Attributes();
		beta.add("fifth", new StringValue("value5"));
		beta.add("seventh", new IntegerValue(7));
		beta.add("eighth", new ByteArrayValue(new byte[]{8, 8}));

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

		assertEquals(alpha.getValue("first"), pp.getAlpha().getValue("first"));
		assertEquals(alpha.getValue("second"), pp.getAlpha().getValue("second"));
		assertEquals(alpha.getValue("third"), pp.getAlpha().getValue("third"));
		assertEquals(alpha.getValue("fourth"), pp.getAlpha().getValue("fourth"));
		assertEquals(null, pp.getAlpha().getValue("fifth"));

		assertEquals(beta.getValue("fifth"), pp.getBeta().getValue("fifth"));
		assertEquals(beta.getValue("sixth"), pp.getBeta().getValue("sixth"));
		assertEquals(beta.getValue("seventh"), pp.getBeta().getValue("seventh"));
		assertEquals(beta.getValue("eighth"), pp.getBeta().getValue("eighth"));
		assertEquals(null, pp.getBeta().getValue("first"));

	}

	/**
	 * Test the conversion from a PersistedPost to a Document
	 */
	@Test
	public void toDbObjectTest() {
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

		assertEquals(alpha.getValue("first").getValue(), ((Document) dbObj.get("alpha")).get("first"));
		assertEquals(alpha.getValue("second").getValue(), ((Document) dbObj.get("alpha")).get("second"));
		assertArrayEquals(((ByteArrayValue) alpha.getValue("third")).getValue(),
				((Binary) ((Document) dbObj.get("alpha")).get("third")).getData());
		assertEquals(alpha.getValue("fourth").getValue(), ((Document) dbObj.get("alpha")).get("fourth"));

		assertTrue(((Document) dbObj.get("beta")).containsKey("fifth"));
		assertTrue(((Document) dbObj.get("beta")).containsKey("seventh"));
		assertTrue(((Document) dbObj.get("beta")).containsKey("eighth"));

		assertEquals(beta.getValue("fifth").getValue(), ((Document) dbObj.get("beta")).get("fifth"));
		assertEquals(beta.getValue("seventh").getValue(), ((Document) dbObj.get("beta")).get("seventh"));
		assertArrayEquals(((ByteArrayValue) beta.getValue("eighth")).getValue(),
				((Binary) ((Document) dbObj.get("beta")).get("eighth")).getData());

	}

	/**
	 * Test the conversion from Document to PersistedPost
	 */
	@Test
	public void fromDbObjectTest() {
		Document dbObj = pp.toDocument();

		PersistedPost newPP = PersistedPost.fromDocument(dbObj);

		assertEquals(this.message[0], newPP.getMessage()[0]);
		assertEquals(this.message[1], newPP.getMessage()[1]);
		assertEquals(this.message[2], newPP.getMessage()[2]);
		assertEquals(this.message[3], newPP.getMessage()[3]);

		assertEquals(this.alpha.getValue("first"), newPP.getAlpha().getValue("first"));
		assertEquals(this.alpha.getValue("second"), newPP.getAlpha().getValue("second"));
		assertEquals(this.alpha.getValue("third"), newPP.getAlpha().getValue("third"));
		assertEquals(this.alpha.getValue("fourth"), newPP.getAlpha().getValue("fourth"));

		assertEquals(this.beta.getValue("fifth"), newPP.getBeta().getValue("fifth"));
		assertEquals(this.beta.getValue("seventh"), newPP.getBeta().getValue("seventh"));
		assertEquals(this.beta.getValue("eighth"), newPP.getBeta().getValue("eighth"));
	}

	/**
	 * Test the use of a JSON string as Message
	 *
	 * @throws UnsupportedEncodingException
	 */
	@Test
	public void jsonMessageTest() throws UnsupportedEncodingException {
		String jsonMessage = "{ \"alpha\" : [ { \"first\" : \"value1\"} , { \"second\" : \"value2\"}] , \"beta\" : [ { \"fifth\" : \"value5\"} , { \"sixth\" : \"value6\"}]}";

		PersistedPost pp2 = new PersistedPost(jsonMessage.getBytes("UTF-8"), alpha, beta);

		Document dbObj = pp2.toDocument();

		assertEquals(jsonMessage, JSON.serialize(dbObj.get("searchable-message")));

		PersistedPost pp3 = PersistedPost.fromDocument(dbObj);

		assertEquals(jsonMessage, new String(pp3.getMessage(), "UTF-8"));

	}

}
