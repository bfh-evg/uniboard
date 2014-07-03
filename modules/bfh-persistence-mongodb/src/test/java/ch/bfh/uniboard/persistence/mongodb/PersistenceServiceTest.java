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
package ch.bfh.uniboard.persistence.mongodb;

import static ch.bfh.uniboard.persistence.mongodb.ConnectionManager.port;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Between;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.DoubleValue;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEqual;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEqual;
import ch.bfh.uniboard.service.NotEqual;
import ch.bfh.uniboard.service.PostElement;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class of persistence componenent
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class PersistenceServiceTest {

	private static PersistenceService pc;
	private static byte[] message;
	private static Attributes alpha;
	private static Attributes beta;
	private static PersistedPost pp;

	private static byte[] message2;
	private static Attributes alpha2;
	private static Attributes beta2;
	private static PersistedPost pp2;

	private static final MongodStarter starter = MongodStarter.getDefaultInstance();

	private static MongodExecutable mongodExe;
	private static MongodProcess mongod;
	private static DBCollection collection;

	public PersistenceServiceTest() {
	}

	/**
	 * Prepares two Post that will be used in the tests
	 */
	@BeforeClass
	public static void setUpClass() throws IOException {

		//Download and start a mongodb deamon for testing
		//TODO use port defined in config
		mongodExe = starter.prepare(new MongodConfigBuilder()
				.version(Version.Main.PRODUCTION)
				.net(new Net(ConnectionManager.port, Network.localhostIsIPv6()))
				.build());
		mongod = mongodExe.start();

		MongoClient mongoClient = new MongoClient("localhost", port);
		//Create or load the database
		DB db = mongoClient.getDB("testDB");

		//create or load the collection
		collection = db.getCollection("test");
		if (collection == null) {
			collection = db.createCollection("test", null);
		}

		pc = new PersistenceService();
		//pc.init();

		message = new byte[]{1, 2, 3, 4};

		alpha = new Attributes();
		alpha.add("first", new StringValue("value1"));
		alpha.add("second", new IntegerValue(2));
		alpha.add("third", new ByteArrayValue(new byte[]{3, 3}));
		alpha.add("fourth", new DateValue(new Date(System.currentTimeMillis())));

		beta = new Attributes();
		beta.add("fifth", new StringValue("value5"));
		beta.add("sixth", new DoubleValue(0.5));
		beta.add("seventh", new IntegerValue(7));
		beta.add("eighth", new ByteArrayValue(new byte[]{8, 8}));

		pp = new PersistedPost(message, alpha, beta);

		try {
			message2 = "{ \"sub1\" : { \"subsub1\" : \"subsubvalue1\"} , \"sub2\" : 2}".getBytes("UTF-8");
		} catch (UnsupportedEncodingException ex) {
			Logger.getLogger(PersistenceServiceTest.class.getName()).log(Level.SEVERE, null, ex);
		}

		alpha2 = new Attributes();
		alpha2.add("first", new StringValue("value12"));
		alpha2.add("second", new IntegerValue(22));
		alpha2.add("third", new ByteArrayValue(new byte[]{3, 3, 2}));
		alpha2.add("fourth", new DateValue(new Date(System.currentTimeMillis() + 100)));

		beta2 = new Attributes();
		beta2.add("fifth", new StringValue("value52"));
		beta2.add("sixth", new DoubleValue(0.52));
		beta2.add("seventh", new IntegerValue(72));
		beta2.add("eighth", new ByteArrayValue(new byte[]{8, 8, 2}));

		pp2 = new PersistedPost(message2, alpha2, beta2);
	}

	@AfterClass
	public static void tearDownClass() {
		//stops the mongodb deamon started
		mongod.stop();
		mongodExe.stop();
	}

	@Before
	public void setUp() {

	}

	@After
	public void tearDown() {
		//empties the DB after each test
		collection.remove(pp.toDBObject());
		collection.remove(pp2.toDBObject());
	}

	/**
	 * Test Post method
	 */
	@Test
	public void postTest() {
		Attributes returned = pc.post(message, alpha, beta);

		DBCursor cursor = collection.find();

		assertEquals(1, cursor.size());
		assertEquals(beta, returned);

		cursor = collection.find(pp.toDBObject());

		assertEquals(1, cursor.size());

		DBObject query = new BasicDBObject();
		query.put("alpha.first", "value1");

		cursor = collection.find(query);

		assertEquals(1, cursor.size());

		assertEquals(pp, PersistedPost.fromDBObject(cursor.next()));
	}

	/**
	 * Test searching a value in the message which is a JSON string
	 */
	@Test
	public void inMessageQueryTest() {
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("sub1");
		keys.add("subsub1");
		constraints.add(new Equal(new StringValue("subsubvalue1"), keys, PostElement.MESSAGE));
		List<String> keys2 = new ArrayList<>();
		keys2.add("sub2");
		constraints.add(new Equal(new IntegerValue(2), keys2, PostElement.MESSAGE));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
	}

	/**
	 * Test Equal constraint for String Type
	 */
	@Test
	public void equalsStringQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");
		constraints.add(new Equal(new StringValue("value1"), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test Equal constraint for Integer Type
	 */
	@Test
	public void equalsIntegerQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("second");
		constraints.add(new Equal(new IntegerValue(2), keys, PostElement.ALPHA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test Equal constraint for Double Type
	 */
	@Test
	public void equalsDoubleQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("sixth");
		constraints.add(new Equal(new DoubleValue(0.5), keys, PostElement.BETA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test Equal constraint for ByteArray Type
	 */
	@Test
	public void equalsByteArrayQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("third");
		constraints.add(new Equal(new ByteArrayValue(new byte[]{3, 3}), keys, PostElement.ALPHA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test Equal constraint for Date Type
	 */
	@Test
	public void equalsDateQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("fourth");
		constraints.add(new Equal((DateValue) pp.getAlpha().getValue("fourth"), keys, PostElement.ALPHA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test multiple Equal constraint for String Type
	 */
	@Test
	public void multipleEqualsStringQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");
		constraints.add(new Equal(new StringValue("value1"), keys, PostElement.ALPHA));
		List<String> keys2 = new ArrayList<>();
		keys2.add("fifth");
		constraints.add(new Equal(new StringValue("value5"), keys2, PostElement.BETA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/**
	 * Test NotEqual constraint for String Type
	 */
	@Test
	public void notEqualsStringQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");
		constraints.add(new NotEqual(new StringValue("value4"), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for String Type
	 */
	@Test
	public void inStringQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");
		List<Value> values = new ArrayList<>();
		values.add(new StringValue("value0"));
		values.add(new StringValue("value1"));
		values.add(new StringValue("value12"));
		constraints.add(new In(values, keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for Integer Type
	 */
	@Test
	public void inIntegerQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("second");
		List<Value> values = new ArrayList<>();
		values.add(new IntegerValue(1));
		values.add(new IntegerValue(2));
		values.add(new IntegerValue(22));
		constraints.add(new In(values, keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for Double Type
	 */
	@Test
	public void inDoubleQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("sixth");
		List<Value> values = new ArrayList<>();
		values.add(new DoubleValue(0.5));
		values.add(new DoubleValue(0.52));
		values.add(new DoubleValue(0.22));
		constraints.add(new In(values, keys, PostElement.BETA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for byte[] Type
	 */
	@Test
	public void inByteArrayQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("third");
		List<Value> values = new ArrayList<>();
		values.add(new ByteArrayValue(new byte[]{1, 1}));
		values.add(new ByteArrayValue(new byte[]{3, 3}));
		values.add(new ByteArrayValue(new byte[]{3, 3, 2}));
		constraints.add(new In(values, keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test In constraint for Date Type
	 */
	@Test
	public void inDateQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("fourth");
		List<Value> values = new ArrayList<>();
		values.add(new DateValue(new Date(System.currentTimeMillis())));
		values.add(pp.getAlpha().getValue("fourth"));
		values.add(pp2.getAlpha().getValue("fourth"));
		constraints.add(new In(values, keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test Between constraint for String Type
	 */
	@Test
	public void betweenStringQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");
		constraints.add(new Between(new StringValue("a"), new StringValue("z"), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
	}

	/**
	 * Test Between constraint for Integer Type
	 */
	@Test
	public void betweenIntegerQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("second");
		constraints.add(new Between(new IntegerValue(1), new IntegerValue(4), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
	}

	/**
	 * Test Between constraint for Double Type
	 */
	@Test
	public void betweenDoubleQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("sixth");
		constraints.add(new Between(new DoubleValue(0.1), new DoubleValue(0.6), keys, PostElement.BETA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test Between constraint for byte[] Type
	 */
	@Test
	public void betweenByteArrayQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("third");
		constraints.add(new Between(new ByteArrayValue(new byte[]{0}), new ByteArrayValue(new byte[]{9, 9, 9, 9}), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
	}

	/**
	 * Test Between constraint for Date Type
	 */
	@Test
	public void betweenDateQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("fourth");
		DateValue startDate = new DateValue(new Date(((Date) pp.getAlpha().getValue("fourth").getValue()).getTime() - 1000));
		DateValue endDate = new DateValue(new Date(System.currentTimeMillis()));

		constraints.add(new Between(startDate, endDate, keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(2, rc.getResult().size());

		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));
	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for String
	 */
	@Test
	public void greaterLessStringQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("first");

		//test greater
		constraints.add(new Greater(new StringValue("value1"), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new StringValue("value1"), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new StringValue("value12"), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new StringValue("value12"), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for Integer
	 */
	@Test
	public void greaterLessIntegerQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("second");

		//test greater
		constraints.add(new Greater(new IntegerValue(2), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new IntegerValue(2), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new IntegerValue(22), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new IntegerValue(22), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for Double
	 */
	@Test
	public void greaterLessDoubleQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("sixth");

		//test greater
		constraints.add(new Greater(new DoubleValue(0.5), keys, PostElement.BETA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new DoubleValue(0.5), keys, PostElement.BETA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new DoubleValue(0.52), keys, PostElement.BETA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new DoubleValue(0.52), keys, PostElement.BETA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for byte[]
	 */
	@Test
	public void greaterLessByteArrayQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("third");

		//test greater
		constraints.add(new Greater(new ByteArrayValue(new byte[]{3, 3}), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(new ByteArrayValue(new byte[]{3, 3}), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(new ByteArrayValue(new byte[]{3, 3, 2}), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(new ByteArrayValue(new byte[]{3, 3, 2}), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test Less, LessEqual, Greater, GreaterEqual queries for Date
	 */
	@Test
	public void greaterLessDateQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();
		List<String> keys = new ArrayList<>();
		keys.add("fourth");

		//test greater
		constraints.add(new Greater(pp.getAlpha().getValue("fourth"), keys, PostElement.ALPHA));
		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp2));

		//test greater equals
		constraints.clear();
		constraints.add(new GreaterEqual(pp.getAlpha().getValue("fourth"), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

		//test less
		constraints.clear();
		constraints.add(new Less(pp2.getAlpha().getValue("fourth"), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));

		//test less equals
		constraints.clear();
		constraints.add(new LessEqual(pp2.getAlpha().getValue("fourth"), keys, PostElement.ALPHA));
		q = new Query(constraints);
		rc = pc.get(q);

		assertEquals(2, rc.getResult().size());
		assertTrue(rc.getResult().contains(pp));
		assertTrue(rc.getResult().contains(pp2));

	}

	/**
	 * Test a complex query containing different Types and Different Constraints
	 */
	@Test
	public void complexQueryTest() {
		pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
		pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());

		List<Constraint> constraints = new ArrayList<>();

		//String Equal
		List<String> keys1 = new ArrayList<>();
		keys1.add("first");
		constraints.add(new Equal(new StringValue("value1"), keys1, PostElement.ALPHA));

		//Integer GreaterEqual
		List<String> keys2 = new ArrayList<>();
		keys2.add("second");
		constraints.add(new GreaterEqual(new IntegerValue(2), keys2, PostElement.ALPHA));

		//byte[] equals
		List<String> keys3 = new ArrayList<>();
		keys3.add("third");
		constraints.add(new Equal(new ByteArrayValue(new byte[]{3, 3}), keys3, PostElement.ALPHA));

		//Date Less
		List<String> keys4 = new ArrayList<>();
		keys4.add("fourth");
		constraints.add(new Less(new DateValue(new Date(System.currentTimeMillis())), keys4, PostElement.ALPHA));

		//String NotEqual
		List<String> keys5 = new ArrayList<>();
		keys5.add("fifth");
		constraints.add(new NotEqual(new StringValue("notthisstring"), keys5, PostElement.BETA));

		//Double In
		List<String> keys6 = new ArrayList<>();
		keys6.add("sixth");
		List<Value> values = new ArrayList<>();
		values.add(new DoubleValue(0.2));
		values.add(new DoubleValue(0.5));
		values.add(new DoubleValue(0.9));
		values.add(new DoubleValue(1.2));
		values.add(new DoubleValue(1.5));
		values.add(new DoubleValue(1.8));
		values.add(new DoubleValue(2.1));
		constraints.add(new In(values, keys6, PostElement.BETA));

		//Integer Between
		List<String> keys7 = new ArrayList<>();
		keys7.add("seventh");
		constraints.add(new Between(new IntegerValue(2), new IntegerValue(18), keys7, PostElement.BETA));

		//byte[] NotEqual
		List<String> keys8 = new ArrayList<>();
		keys8.add("eighth");
		constraints.add(new NotEqual(new ByteArrayValue(new byte[]{1}), keys8, PostElement.BETA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertEquals(1, rc.getResult().size());
		assertEquals(pp, rc.getResult().get(0));
	}

	/* -------------------------------
	 * ERROR TESTING
	 * ------------------------------- */
	/**
	 * Test Post method with a null parameter
	 */
	@Test
	public void postNullTest() {
		Attributes returned = pc.post(message, null, beta);

		assertTrue(returned.getKeys().contains(Attributes.REJECTED));

		DBCursor cursor = collection.find();

		assertEquals(0, cursor.size());

	}

	/**
	 * Test Get method with a null parameter
	 */
	@Test
	public void getNullTest() {
		ResultContainer rc = pc.get(null);

		assertTrue(rc.getGamma().getKeys().contains(Attributes.REJECTED));
		assertEquals(0, rc.getResult().size());

	}

	/**
	 * Test query constituted of a In constraint conataining different Value types
	 */
	@Test
	public void queryInDifferentTypeTest() {

		List<Constraint> constraints = new ArrayList<>();

		List<String> keys = new ArrayList<>();
		keys.add("sixth");
		List<Value> values = new ArrayList<>();
		values.add(new DoubleValue(0.2));
		values.add(new IntegerValue(5));
		values.add(new DoubleValue(0.9));
		values.add(new StringValue("1.5"));
		constraints.add(new In(values, keys, PostElement.BETA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertTrue(rc.getGamma().getKeys().contains(Attributes.REJECTED));
		assertEquals(0, rc.getResult().size());
	}

	/**
	 * Test query consititued of a Between constraint conataining different Value types
	 */
	@Test
	public void queryBetweenDifferentTypeTest() {

		List<Constraint> constraints = new ArrayList<>();

		List<String> keys = new ArrayList<>();
		keys.add("seventh");
		constraints.add(new Between(new IntegerValue(2), new ByteArrayValue(new byte[]{1}), keys, PostElement.BETA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

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
		constraints.add(new Equal(null, keys, PostElement.ALPHA));

		Query q = new Query(constraints);
		ResultContainer rc = pc.get(q);

		assertTrue(rc.getGamma().getKeys().contains(Attributes.ERROR));
		assertEquals(0, rc.getResult().size());
	}

}
