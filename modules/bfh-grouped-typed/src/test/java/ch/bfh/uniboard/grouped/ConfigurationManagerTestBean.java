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
package ch.bfh.uniboard.grouped;

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

	private boolean correct = true;

	@Override
	public Configuration getConfiguration(String key) {
		if (this.correct) {
			Configuration p = new Configuration();
			p.getEntries().put("number", "{\"type\" : \"number\"}");
			p.getEntries().put("ip", "{\"$schema\" : \"http://json-schema.org/draft-03/schema#\",\"format\" : \"ip-address\"}");
			return p;
		}
		return null;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	@Override
	public void saveState(State state) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T extends State> T loadState(String key, Class<T> t) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
