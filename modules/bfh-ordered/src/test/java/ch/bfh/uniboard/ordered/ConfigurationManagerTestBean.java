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
package ch.bfh.uniboard.ordered;

import ch.bfh.uniboard.service.Configuration;
import ch.bfh.uniboard.service.ConfigurationManager;
import ch.bfh.uniboard.service.State;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@LocalBean
public class ConfigurationManagerTestBean implements ConfigurationManager {

	private State saved;

	@Override
	public Configuration getConfiguration(String key) {
		return null;
	}

	@Override
	public void saveState(State configuration) {
		this.saved = configuration;
	}

	public State getSaved() {
		return this.saved;
	}

	@Override
	public <T extends State> T loadState(String key, Class<T> t) {
		OrderedState state = new OrderedState();
		state.getSections().put("section1", 1);
		state.getSections().put("section2", 26);
		return (T) state;
	}

}
