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

import ch.bfh.uniboard.service.data.Equal;
import ch.bfh.uniboard.service.data.GreaterEqual;
import ch.bfh.uniboard.service.data.ResultContainer;
import ch.bfh.uniboard.service.data.Between;
import ch.bfh.uniboard.service.data.In;
import ch.bfh.uniboard.service.data.Order;
import ch.bfh.uniboard.service.data.Less;
import ch.bfh.uniboard.service.data.Greater;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.NotEqual;
import ch.bfh.uniboard.service.data.LessEqual;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.Constraint;
import ch.bfh.uniboard.service.data.MessageIdentifier;
import ch.bfh.uniboard.service.*;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.uniboard.service.data.PropertyIdentifier;
import ch.bfh.uniboard.service.data.PropertyIdentifierType;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import org.bson.Document;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test class of persistence component
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class PersistenceServiceTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addClass(PersistenceService.class)
				.addClass(PersistedPost.class)
				.addClass(ConnectionManagerTestImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return ja;
	}
	@EJB
	private PostService ps;
	@EJB
	private GetService gs;
	@EJB
	ConnectionManager conManager;
	private static byte[] message;
	private static Attributes alpha;
	private static Attributes beta;
	private static PersistedPost pp;

	private static byte[] message2;
	private static Attributes alpha2;
	private static Attributes beta2;
	private static PersistedPost pp2;

	public PersistenceServiceTest() {
	}

	/**
	 * Prepares two Post that will be used in the tests
	 */
	@BeforeClass
	public static void setUpClass() throws IOException {

		message = new byte[]{1, 2, 3, 4};

		alpha = new Attributes();
		alpha.add("first", "value1");
		alpha.add("second", "2");
		alpha.add("third", "0xNDEwMjEwOTM5MDZaFw0xNjEwMjEwnO");
		alpha.add("fourth", "2014-10-15T13:00:00Z");

		beta = new Attributes();
		beta.add("fifth", "value5");
		beta.add("seventh", "7");
		beta.add("eighth", "0xNDEwMjEwOTM5MDZaFw0xNjEwMjEwnO");

		pp = new PersistedPost(message, alpha, beta);

		message2 = "{ \"sub1\" : { \"subsub1\" : \"subsubvalue1\"} , \"sub2\" : 2}".getBytes("UTF-8");

		alpha2 = new Attributes();
		alpha2.add("first", "value12");
		alpha2.add("second", "22");
		alpha2.add("third", "0xNDEwMjEwOTM5MDZaFw0xNjEwMjEwnO");
		alpha2.add("fourth", "2015-10-15T13:00:00Z");

		beta2 = new Attributes();
		beta2.add("fifth", "value55");
		beta2.add("seventh", "77");
		beta2.add("eighth", "0xNDEwMjEwOTM5MDZaFw0xNjEwMjEwnO");

		pp2 = new PersistedPost(message2, alpha2, beta2);
	}

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {
		//empties the DB after each test
		//conManager.getCollection().remove(pp.toDBObject());
		//conManager.getCollection().remove(pp2.toDBObject());
		conManager.getCollection("uniboard").drop();
	}

	/**
	 * Test Post method
	 */
	@Test
	public void postTest() {
		Attributes returned = ps.post(message, alpha, beta);

		FindIterable<Document> cursor = conManager.getCollection("uniboard").find();

		assertEquals(1, conManager.getCollection("uniboard").count());
		assertEquals(beta, returned);

		cursor = conManager.getCollection("uniboard").find(pp.toDocument());

		BasicDBObject query = new BasicDBObject();
		query.put("alpha.first", "value1");

		assertEquals(1, conManager.getCollection("uniboard").count(query));

		assertEquals(pp, PersistedPost.fromDocument(
				conManager.getCollection("uniboard").find(query).first()));
	}

	/**
	 * Test searching a value in the message which is a JSON string
	 */
	@Test
	public void inMessageQueryTest() {
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		String key = "sub1.subsub1";
		constraints.add(new Equal(new MessageIdentifier(key, DataType.STRING), "subsubvalue1"));
		String key2 = "sub2";
		constraints.add(new Equal(new MessageIdentifier(key2, DataType.INTEGER), "2"));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
	}

	/**
	 * Test Equal constraint for String Type
	 */
	@Test
	public void equalsStringQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new Equal(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "first"), "value1"));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test Equal constraint for Integer Type
	 */
	@Test
	public void equalsIntegerQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new Equal(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "second"), "2"));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test Equal constraint for ByteArray Type
	 */
	@Test
	public void equalsByteArrayQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new Equal(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "third"), "0xNDEwMjEwOTM5MDZaFw0xNjEwMjEwnO"));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test Equal constraint for Date Type
	 */
	@Test
	public void equalsDateQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		constraints.add(new Equal(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "fourth"), "2014-10-15T13:00:00Z"));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test multiple Equal constraint for String Type
	 */
	@Test
	public void multipleEqualsStringQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new Equal(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "first"), "value1"));

		constraints.add(new Equal(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "fifth"), "value5"));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test NotEqual constraint for String Type
	 */
	@Test
	public void notEqualsStringQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new NotEqual(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "first"), "value4"));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for String Type
	 */
	@Test
	public void inStringQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");
		List<String> values = new ArrayList<>();
		values.add("value0");
		values.add("value1");
		values.add("value12");
		constraints.add(new In(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "first"), values));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for Integer Type
	 */
	@Test
	public void inIntegerQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("second");
		List<String> values = new ArrayList<>();
		values.add("1");
		values.add("2");
		values.add("22");
		constraints.add(new In(new MessageIdentifier("sub2", DataType.INTEGER), values));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for byte[] Type
	 */
//	@Test
//	public void inByteArrayQueryTest() {
//		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
//		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());
//
//		List<Constraint> constraints = new ArrayList<>();
//		List<String> keys = new ArrayList<>();
//		keys.add("third");
//		List<Value> values = new ArrayList<>();
//		values.add(new ByteArrayValue(new byte[]{1, 1}));
//		values.add(new ByteArrayValue(new byte[]{3, 3}));
//		values.add(new ByteArrayValue(new byte[]{3, 3, 2}));
//		constraints.add(new In(new AlphaIdentifier(keys), values));
//		Query q = new Query(constraints);
//		ResultContainer rc = gs.get(q);
//
//		assertEquals(2, rc.getResult().size());
//
//		assertTrue(rc.getResult().contains(pp));
//		assertTrue(rc.getResult().contains(pp2));
//	}
	/**
	 * Test In constraint for Date Type
	 */
	@Test
	public void inDateQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> values = new ArrayList<>();
		values.add("2066-10-15T13:00:00Z");
		values.add(pp.getAlpha().getValue("fourth"));
		values.add(pp2.getAlpha().getValue("fourth"));
		constraints.add(new In(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "fourth"), values));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test Between constraint for String Type
	 */
	@Test
	public void betweenStringQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new Between(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "first"), "a", "z"));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
	}

	/**
	 * Test Between constraint for Integer Type
	 */
	@Test
	public void betweenIntegerQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		constraints.add(new Between(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "second"), "1", "4"));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
	}

	/**
	 * Test Between constraint for byte[] Type
	 */
	@Test
	public void betweenByteArrayQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("third");
		constraints.add(new Between(new AlphaIdentifier(keys), new ByteArrayValue(new byte[]{0}), new ByteArrayValue(new byte[]{9, 9, 9, 9})));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
	}

	/**
	 * Test Between constraint for Date Type
	 */
	@Test
	public void betweenDateQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("fourth");
		String startDate = "2014-10-15T12:00:00Z";
		String endDate = "2015-10-15T14:00:00Z";

		constraints.add(new Between(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "fourth"), startDate, endDate));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for String
	 */
	@Test
	public void greaterLessStringQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");

		//test greater
		constraints.add(new Greater(new AlphaIdentifier(keys), new StringValue("value1")));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new AlphaIdentifier(keys), new StringValue("value1")));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new AlphaIdentifier(keys), new StringValue("value12")));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new AlphaIdentifier(keys), new StringValue("value12")));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for Integer
	 */
	@Test
	public void greaterLessIntegerQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("second");

		//test greater
		constraints.add(new Greater(new AlphaIdentifier(keys), new IntegerValue(2)));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new AlphaIdentifier(keys), new IntegerValue(2)));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new AlphaIdentifier(keys), new IntegerValue(22)));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new AlphaIdentifier(keys), new IntegerValue(22)));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for byte[]
	 */
	@Test
	public void greaterLessByteArrayQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("third");

		//test greater
		constraints.add(new Greater(new AlphaIdentifier(keys), new ByteArrayValue(new byte[]{3, 3})));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new AlphaIdentifier(keys), new ByteArrayValue(new byte[]{3, 3})));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new AlphaIdentifier(keys), new ByteArrayValue(new byte[]{3, 3, 2})));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new AlphaIdentifier(keys), new ByteArrayValue(new byte[]{3, 3, 2})));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for Date
	 */
	@Test
	public void greaterLessDateQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("fourth");

		//test greater
		constraints.add(new Greater(new AlphaIdentifier(keys), pp.getAlpha().getValue("fourth")));
		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new AlphaIdentifier(keys), pp.getAlpha().getValue("fourth")));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new AlphaIdentifier(keys), pp2.getAlpha().getValue("fourth")));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new AlphaIdentifier(keys), pp2.getAlpha().getValue("fourth")));
		q = new Query(constraints);
		rc = gs.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test a complex query containing different Types and Different Constraints
	 */
	@Test
	public void complexQueryTest() {
		ps.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		ps.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();

		//String Equal
		List<String> keys1 = new ArrayList<>();
		keys1.add("first");
		constraints.add(new Equal(new AlphaIdentifier(keys1), new StringValue("value1")));

		//Integer GreaterEqual
		List<String> keys2 = new ArrayList<>();
		keys2.add("second");
		constraints.add(new GreaterEqual(new AlphaIdentifier(keys2), new IntegerValue(2)));

		//byte[] equals
		List<String> keys3 = new ArrayList<>();
		keys3.add("third");
		constraints.add(new Equal(new AlphaIdentifier(keys3), new ByteArrayValue(new byte[]{3, 3})));

		//Date Less
		List<String> keys4 = new ArrayList<>();
		keys4.add("fourth");
		constraints.add(new Less(new AlphaIdentifier(keys4), new DateValue(new Date(System.currentTimeMillis()))));

		//String NotEqual
		List<String> keys5 = new ArrayList<>();
		keys5.add("fifth");
		constraints.add(new NotEqual(new BetaIdentifier(keys5), new StringValue("notthisstring")));

		//Integer Between
		List<String> keys7 = new ArrayList<>();
		keys7.add("seventh");
		constraints.add(new Between(new BetaIdentifier(keys7), new IntegerValue(2), new IntegerValue(18)));

		//byte[] NotEqual
		List<String> keys8 = new ArrayList<>();
		keys8.add("eighth");
		constraints.add(new NotEqual(new BetaIdentifier(keys8), new ByteArrayValue(new byte[]{1})));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/* -------------------------------
	 * ERROR TESTING
	 * ------------------------------- */
	/**
	 * Test query constituted of a In constraint containing different Value types
	 */
	@Test
	public void queryInDifferentTypeTest() {

		List<Constraint> constraints = new ArrayList<>();

		List<String> keys = new ArrayList<>();
		keys.add("sixth");
		List<Value> values = new ArrayList<>();
		values.add(new IntegerValue(5));
		values.add(new StringValue("1.5"));
		constraints.add(new In(new BetaIdentifier(keys), values));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertTrue(rc.getGamma().getKeys().contains(Attributes.REJECTED));
		assertEquals(0, rc.getResult().size());
	}

	/**
	 * Test query constituted of a Between constraint containing different Value types
	 */
	@Test
	public void queryBetweenDifferentTypeTest() {

		List<Constraint> constraints = new ArrayList<>();

		List<String> keys = new ArrayList<>();
		keys.add("seventh");
		constraints.add(new Between(new BetaIdentifier(keys), new IntegerValue(2), new ByteArrayValue(new byte[]{1})));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertTrue(rc.getGamma().getKeys().contains(Attributes.REJECTED));
		assertEquals(0, rc.getResult().size());
	}

	/**
	 * Test query constituted of a Between constraint containing different Value types
	 */
	@Test
	public void queryNullTypeTest() {

		List<Constraint> constraints = new ArrayList<>();

		//Integer Between
		List<String> keys = new ArrayList<>();
		keys.add("first");
		constraints.add(new Equal(new AlphaIdentifier(keys), null));

		Query q = new Query(constraints);
		ResultContainer rc = gs.get(q);

		assertTrue(rc.getGamma().getKeys().contains(Attributes.ERROR));
		assertEquals(0, rc.getResult().size());
	}

	/* -------------------------------
	 * Order Testing
	 * ------------------------------- */
	/**
	 * Test that multiple elements with ascending order are returned
	 */
	@Test
	public void oneOrderAsc() {
		Attributes a1 = new Attributes();
		a1.add("first", new StringValue("value1"));

		Attributes a2 = new Attributes();
		a2.add("first", new StringValue("value2"));

		Attributes a3 = new Attributes();
		a3.add("first", new StringValue("value3"));

		ps.post(message, a2, beta);
		ps.post(message, a1, beta);
		ps.post(message, a3, beta);

		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new BetaIdentifier("fifth"), new StringValue("value5"));
		constraints.add(c);

		List<Order> orderBy = new ArrayList<>();
		Order order = new Order(new AlphaIdentifier("first"), true);
		orderBy.add(order);

		Query q = new Query(constraints, orderBy);

		ResultContainer rc = gs.get(q);

		assertEquals(3, rc.getResult().size());
		String s = ((StringValue) rc.getResult().get(0).getAlpha().getValue("first")).getValue();
		assertEquals("value1", s);

		String s2 = ((StringValue) rc.getResult().get(1).getAlpha().getValue("first")).getValue();
		assertEquals("value2", s2);

		String s3 = ((StringValue) rc.getResult().get(2).getAlpha().getValue("first")).getValue();
		assertEquals("value3", s3);

		PersistedPost p1 = new PersistedPost(message, a1, beta);
		PersistedPost p2 = new PersistedPost(message, a2, beta);
		PersistedPost p3 = new PersistedPost(message, a3, beta);
		conManager.getCollection("uniboard").deleteOne(p1.toDocument());
		conManager.getCollection("uniboard").deleteOne(p2.toDocument());
		conManager.getCollection("uniboard").deleteOne(p3.toDocument());
	}

	@Test
	public void oneOrderDesc() {
		Attributes a1 = new Attributes();
		a1.add("first", new StringValue("value1"));

		Attributes a2 = new Attributes();
		a2.add("first", new StringValue("value2"));

		Attributes a3 = new Attributes();
		a3.add("first", new StringValue("value3"));

		ps.post(message, a2, beta);
		ps.post(message, a1, beta);
		ps.post(message, a3, beta);

		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new BetaIdentifier("fifth"), new StringValue("value5"));
		constraints.add(c);

		List<Order> orderBy = new ArrayList<>();
		Order order = new Order(new AlphaIdentifier("first"), false);
		orderBy.add(order);

		Query q = new Query(constraints, orderBy);

		ResultContainer rc = gs.get(q);

		assertEquals(3, rc.getResult().size());
		String s = ((StringValue) rc.getResult().get(0).getAlpha().getValue("first")).getValue();
		assertEquals("value3", s);

		String s2 = ((StringValue) rc.getResult().get(1).getAlpha().getValue("first")).getValue();
		assertEquals("value2", s2);

		String s3 = ((StringValue) rc.getResult().get(2).getAlpha().getValue("first")).getValue();
		assertEquals("value1", s3);

		PersistedPost p1 = new PersistedPost(message, a1, beta);
		PersistedPost p2 = new PersistedPost(message, a2, beta);
		PersistedPost p3 = new PersistedPost(message, a3, beta);
		conManager.getCollection("uniboard").deleteOne(p1.toDocument());
		conManager.getCollection("uniboard").deleteOne(p2.toDocument());
		conManager.getCollection("uniboard").deleteOne(p3.toDocument());
	}

	@Test
	public void multiOrder() {
		Attributes a1 = new Attributes();
		a1.add("second", new StringValue("value1"));
		a1.add("first", new StringValue("value1"));

		Attributes a2 = new Attributes();
		a2.add("second", new StringValue("value2"));
		a2.add("first", new StringValue("value2"));

		Attributes a3 = new Attributes();
		a3.add("second", new StringValue("value3"));
		a3.add("first", new StringValue("value1"));

		ps.post(message, a2, beta);
		ps.post(message, a1, beta);
		ps.post(message, a3, beta);

		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new BetaIdentifier("fifth"), new StringValue("value5"));
		constraints.add(c);

		List<Order> orderBy = new ArrayList<>();
		Order order = new Order(new AlphaIdentifier("first"), true);
		orderBy.add(order);
		Order order2 = new Order(new AlphaIdentifier("second"), true);
		orderBy.add(order2);

		Query q = new Query(constraints, orderBy);

		ResultContainer rc = gs.get(q);

		assertEquals(3, rc.getResult().size());
		String s = ((StringValue) rc.getResult().get(0).getAlpha().getValue("first")).getValue();
		assertEquals("value1", s);
		String s1 = ((StringValue) rc.getResult().get(0).getAlpha().getValue("second")).getValue();
		assertEquals("value1", s1);

		String s2 = ((StringValue) rc.getResult().get(1).getAlpha().getValue("first")).getValue();
		assertEquals("value1", s2);
		String s22 = ((StringValue) rc.getResult().get(1).getAlpha().getValue("second")).getValue();
		assertEquals("value3", s22);

		String s3 = ((StringValue) rc.getResult().get(2).getAlpha().getValue("first")).getValue();
		assertEquals("value2", s3);
		String s33 = ((StringValue) rc.getResult().get(2).getAlpha().getValue("second")).getValue();
		assertEquals("value2", s33);

		PersistedPost p1 = new PersistedPost(message, a1, beta);
		PersistedPost p2 = new PersistedPost(message, a2, beta);
		PersistedPost p3 = new PersistedPost(message, a3, beta);
		conManager.getCollection("uniboard").deleteOne(p1.toDocument());
		conManager.getCollection("uniboard").deleteOne(p2.toDocument());
		conManager.getCollection("uniboard").deleteOne(p3.toDocument());
	}

	/* -------------------------------
	 * Limit Testing
	 * ------------------------------- */
	@Test
	public void testLimit() {
		ps.post(message, alpha, beta);
		ps.post(message, alpha, beta);
		ps.post(message, alpha, beta);
		ps.post(message, alpha, beta);
		ps.post(message, alpha, beta);
		ps.post(message, alpha, beta);

		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new BetaIdentifier("fifth"), new StringValue("value5"));
		constraints.add(c);

		Query q = new Query(constraints, 4);

		ResultContainer rc = gs.get(q);

		assertEquals(4, rc.getResult().size());

	}

	@Test
	public void testLimitWithOrder() {

		Attributes a1 = new Attributes();
		a1.add("first", new StringValue("value9"));
		ps.post(message, a1, beta);
		Attributes a2 = new Attributes();
		a2.add("first", new StringValue("value8"));
		ps.post(message, a2, beta);
		Attributes a3 = new Attributes();
		a3.add("first", new StringValue("value4"));
		ps.post(message, a3, beta);
		Attributes a4 = new Attributes();
		a4.add("first", new StringValue("value2"));
		ps.post(message, a4, beta);
		Attributes a5 = new Attributes();
		a5.add("first", new StringValue("value6"));
		ps.post(message, a5, beta);

		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new BetaIdentifier("fifth"), new StringValue("value5"));
		constraints.add(c);

		List<Order> orderBy = new ArrayList<>();
		Order order = new Order(new AlphaIdentifier("first"), true);
		orderBy.add(order);

		Query q = new Query(constraints, orderBy, 4);

		ResultContainer rc = gs.get(q);

		assertEquals(4, rc.getResult().size());

		String s = ((StringValue) rc.getResult().get(3).getAlpha().getValue("first")).getValue();
		assertEquals("value8", s);
	}
}
