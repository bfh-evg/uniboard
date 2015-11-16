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

import ch.bfh.uniboard.service.ConfigurationManager;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@Startup
public class ObserverManagerImpl implements ObserverManager {

	@EJB
	ConfigurationManager configurationManager;

	private static final String STATE_NAME = "bfh-notification-observer";
	private static final Logger logger = Logger.getLogger(ObserverManagerImpl.class.getName());
	private Map<String, Observer> observers;

	@PostConstruct
	protected void init() {
		Properties state = configurationManager.loadState(STATE_NAME);
		this.observers = new HashMap<>();
		if (state == null) {
			return;
		}
		for (Entry e : state.entrySet()) {
			String key = (String) e.getKey();
			String s = (String) e.getValue();

			try {
				JAXBContext jaxbContext = ObserverManagerImpl.initJAXBContext(Observer.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				Reader reader = new StringReader(s);
				Observer obs = unmarshaller.unmarshal(new StreamSource(reader), Observer.class).getValue();
				this.observers.put(key, obs);
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Could not restore persisted observer.");
			}
		}
	}

	@PreDestroy
	protected void stop() {
		Properties config = new Properties();
		for (Entry<String, Observer> e : this.observers.entrySet()) {
			try {
				JAXBContext jaxbContext = ObserverManagerImpl.initJAXBContext(Observer.class);
				StringWriter writer = new StringWriter();
				Marshaller marshaller = jaxbContext.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
				marshaller.marshal(e.getValue(), writer);
				String tmp = writer.toString();
				config.put(e.getKey(), tmp);
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Could not persist observer to configuraiton.", ex);
			}
		}
		configurationManager.saveState(STATE_NAME, config);
	}

	@Override
	public Map<String, Observer> getObservers() {
		return new HashMap<>(observers);
	}

	@Override
	@Lock(LockType.WRITE)
	public Observer remove(String notificationCode) {
		return this.observers.remove(notificationCode);
	}

	@Override
	@Lock(LockType.WRITE)
	public void put(String notificationCode, Observer observer) {
		this.observers.put(notificationCode, observer);
	}

	/**
	 * Initializes the JAXB context.
	 *
	 * @param <T> the Java type of the domain class the conversion takes place
	 * @param type the actual type object
	 * @return the JAXB context
	 * @throws Exception if the context cannot be established
	 */
	private static <T> JAXBContext initJAXBContext(Class<T> type) throws Exception {
		return JAXBContext.newInstance(new Class<?>[]{type});
	}
}
