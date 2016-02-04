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

import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.uniboard.service.configuration.State;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@LocalBean
public class ConfigurationManagerTestBean implements ConfigurationManager {

	private State state = null;
	private Configuration config = null;

	@Override
	public Configuration getConfiguration(String key) {
		//Rewired for test
		return config;
	}

	public void setConfiguration(Configuration config) {
		this.config = config;
	}

	@Override
	public void saveState(State state) {
		this.state = state;
	}

	@Override
	public <T extends State> T loadState(String key, Class<T> t) {
		return (T) state;
	}

}
