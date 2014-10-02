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

import java.util.HashMap;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@LocalBean
@Singleton
public class ObserverManagerMock implements ObserverManager {

	private Map<String, Observer> observers = new HashMap<>();

	@Override
	public Map<String, Observer> getObservers() {
		return this.observers;
	}

	public void addObserver(String key, Observer observer) {
		this.observers.put(key, observer);
	}

}
