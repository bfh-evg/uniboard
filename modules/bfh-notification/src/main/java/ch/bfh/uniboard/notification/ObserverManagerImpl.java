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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.bind.JAXBContext;

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
	private NotificationState state;

	@PostConstruct
	protected void init() {
		state = configurationManager.loadState(STATE_NAME, NotificationState.class);
		if (state == null) {
			this.state = new NotificationState();
			this.state.setKey(STATE_NAME);
		}
	}

	@PreDestroy
	protected void stop() {
		configurationManager.saveState(state);
	}

	@Override
	public Map<String, Observer> getObservers() {
		return new HashMap<>(state.getObservers());
	}

	@Override
	@Lock(LockType.WRITE)
	public Observer remove(String notificationCode) {
		return this.state.getObservers().remove(notificationCode);
	}

	@Override
	@Lock(LockType.WRITE)
	public void put(String notificationCode, Observer observer) {
		this.state.getObservers().put(notificationCode, observer);
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
