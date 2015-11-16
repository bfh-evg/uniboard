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
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
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
		configurationManager.saveState(null, null);
		observerManager.init();
		Map<String, Observer> observers = observerManager.getObservers();
		assertTrue(observers.isEmpty());
	}

	@Test
	public void testNotEmpty() throws Exception {
		Properties config = new Properties();
		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new AlphaIdentifier("test"), new StringValue("test2"));
		constraints.add(c);
		Query q = new Query(constraints);
		Observer obs = new Observer("URL", q);

		JAXBContext jaxbContext = JAXBContext.newInstance(new Class<?>[]{Observer.class});
		StringWriter writer = new StringWriter();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		marshaller.marshal(obs, writer);
		String tmp = writer.toString();
		config.put("test3", tmp);

		configurationManager.saveState(null, config);
		observerManager.init();
		Map<String, Observer> observers = observerManager.getObservers();
		assertFalse(observers.isEmpty());
	}

	@Test
	public void testShutdown() throws Exception {
		observerManager.init();
		Map<String, Observer> observers = observerManager.getObservers();
		List<Constraint> constraints = new ArrayList<>();
		Constraint c = new Equal(new AlphaIdentifier("test"), new StringValue("test2"));
		constraints.add(c);
		Query q = new Query(constraints);
		Observer obs = new Observer("URL", q);
		observerManager.put("testKey", obs);
		observerManager.stop();

		Properties config = configurationManager.loadState("");
		assertEquals(config.size(), 1);
		String xmlString = config.getProperty("testKey");
		JAXBContext jaxbContext = JAXBContext.newInstance(new Class<?>[]{Observer.class});
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Reader reader = new StringReader(xmlString);
		Observer tmp = unmarshaller.unmarshal(new StreamSource(reader), Observer.class).getValue();
		assertEquals(tmp.getUrl(), "URL");
	}
}
