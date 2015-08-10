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
package ch.bfh.uniboard.sectioned;

import ch.bfh.uniboard.service.ConfigurationManager;
import java.util.Properties;
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
	public Properties getConfiguration(String key) {
		if (this.correct) {
			Properties p = new Properties();
			p.put("section1", "test");
			p.put("section2", "test2");
			return p;
		}
		return null;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	@Override
	public void saveState(String key, Properties configuration) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Properties loadState(String key) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
