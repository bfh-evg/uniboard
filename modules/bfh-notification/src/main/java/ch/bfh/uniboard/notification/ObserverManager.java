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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@Startup
public class ObserverManager {

	@EJB
	ConfigurationManager configurationManager;

	private static final String CONFIG_NAME = "bfh-notification-persistence";
	private static final Logger logger = Logger.getLogger(ObserverManager.class.getName());
	private Map<String, Observer> observers;

	@PostConstruct
	public void init() {
		Properties config = configurationManager.getConfiguration(CONFIG_NAME);
		if (config == null) {
			this.observers = new HashMap<>();
			return;
		}
		for (Entry e : config.entrySet()) {
			String key = (String) e.getKey();
			String s = (String) e.getValue();
			try {
				Observer tmp;
				try (ObjectInputStream objectInputStream
						= new ObjectInputStream(new ByteArrayInputStream(s.getBytes()))) {
					tmp = (Observer) objectInputStream.readObject();
				}
				this.observers.put(key, tmp);
			} catch (IOException | ClassNotFoundException ex) {
				logger.log(Level.WARNING, "Could not restore observer from configuration.", ex);
			}

		}
	}

	@PreDestroy
	public void stop() {
		Properties config = new Properties();
		for (Entry<String, Observer> e : this.observers.entrySet()) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
					oos.writeObject(e.getValue());
				}
				String tmp = new String(baos.toByteArray());
				config.put(e.getKey(), tmp);
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Could not persist observer to configuraiton.", ex);
			}
		}
		configurationManager.saveConfiguration(CONFIG_NAME, config);
	}

	public Map<String, Observer> getObservers() {
		return observers;
	}
}
