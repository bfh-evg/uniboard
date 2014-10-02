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
package ch.bfh.uniboard.notification;

import ch.bfh.uniboard.service.AlphaIdentifier;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.StringValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class ObserverManagerTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive ja = ShrinkWrap.create(WebArchive.class)
				.addClass(ObserverManagerImpl.class)
				.addClass(ObserverManagerFront.class)
				.addClass(ConfigurationManagerTestBean.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(ja.toString(true));
		return ja;
	}

	@EJB
	ObserverManagerFront observerManager;
	@EJB
	ConfigurationManagerTestBean configurationManager;

	public ObserverManagerTest() {
	}

	@Test
	public void testEmpty() {
		configurationManager.saveConfiguration(null, null);
		observerManager.init();
		Map<String, Observer> observers = observerManager.getObservers();
		assertTrue(observers.isEmpty());
	}

	@Test
	public void testNotEmpty() throws IOException {
		Properties config = new Properties();
		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new AlphaIdentifier("test"), new StringValue("test2"));
		constraints.add(c);
		Query q = new Query(constraints);
		Observer obs = new Observer("URL", q);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(obs);
		}
		String tmp = Base64.getEncoder().encodeToString(baos.toByteArray());
		config.put("test3", tmp);

		configurationManager.saveConfiguration(null, config);
		observerManager.init();
		Map<String, Observer> observers = observerManager.getObservers();
		assertFalse(observers.isEmpty());
	}

	@Test
	public void testShutdown() throws IOException, ClassNotFoundException {
		observerManager.init();
		Map<String, Observer> observers = observerManager.getObservers();
		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new AlphaIdentifier("test"), new StringValue("test2"));
		constraints.add(c);
		Query q = new Query(constraints);
		Observer obs = new Observer("URL", q);
		observers.put("testKey", obs);
		observerManager.stop();

		Properties config = configurationManager.getConfiguration("");
		assertEquals(config.size(), 1);
		String b64String = config.getProperty("testKey");
		byte[] data = Base64.getDecoder().decode(b64String);
		Observer tmp;
		try (ObjectInputStream objectInputStream
				= new ObjectInputStream(new ByteArrayInputStream(data))) {
			tmp = (Observer) objectInputStream.readObject();
		}
		assertEquals(tmp.getUrl(), "URL");
	}
}
