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

import java.util.Map;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class ObserverManagerFront extends ObserverManagerImpl {

	@Override
	public Map<String, Observer> getObservers() {
		return super.getObservers();
	}

	@Override
	public void stop() {
		super.stop();
	}

	@Override
	public void init() {
		super.init();
	}

}
