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
package ch.bfh.uniboard.configuration;

import ch.bfh.uniboard.persistence.mongodb.ConnectionManager;
import ch.bfh.uniboard.service.configuration.Configuration;
import com.mongodb.client.MongoCollection;
import static com.mongodb.client.model.Filters.eq;
import javax.ejb.EJB;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class ConfigurationManagerImplTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addClass(TestableConfigurationManagerImpl.class)
				.addClass(ConnectionManagerTestImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return ja;
	}

	@EJB
	TestableConfigurationManagerImpl cm;

	@EJB
	ConnectionManager connectionManager;

	public ConfigurationManagerImplTest() {
	}

	@Test
	public void testGetConfiguration1() throws Exception {
		String collectionName = "uniboard-configuration";
		MongoCollection<Document> collection = this.connectionManager.getCollection(collectionName);
		String cdfgString = "{\"config_key\":\"testKey1\",\"entries\":{\"testkey\":\"testvalue\"}}";
		Document config = Document.parse(cdfgString);
		collection.insertOne(config);
		cm.init();
		Configuration p = cm.getConfiguration("testKey1");
		assertNotNull(p);
		String result = p.getEntries().get("testkey");
		assertEquals(result, "testvalue");
	}

	@Test
	public void testSaveState1() throws Exception {
		String collectionName = "uniboard-configuration";
		MongoCollection<Document> collection = this.connectionManager.getCollection(collectionName);
		TestState state = new TestState();
		state.setKey("testSaveState1");
		state.setTest1("test");
		state.setTest2(2);

		cm.saveState(state);
		Bson query = eq("state_key", state.getKey());
		Document result = collection.find(query).first();
		assertTrue(result.containsKey("test1"));
		assertEquals("test", result.getString("test1"));
		assertTrue(result.containsKey("test2"));
		assertEquals(new Integer("2"), result.getInteger("test2"));

	}

	@Test
	public void testLoadState() throws Exception {
		String collectionName = "uniboard-configuration";
		MongoCollection<Document> collection = this.connectionManager.getCollection(collectionName);
		String cdfgString = "{\"type\":\"testState\",\"state_key\":\"testLoadState\",\"test1\":\"test\",\"test2\":2}";
		Document config = Document.parse(cdfgString);
		collection.insertOne(config);
		cm.init();
		TestState state = cm.loadState("testLoadState", TestState.class);
		assertNotNull(state);
		assertEquals("test", state.getTest1());
		assertEquals(2, state.getTest2());
	}

}
