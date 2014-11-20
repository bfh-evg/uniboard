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

	private Properties saved;

	@Override
	public Properties getConfiguration(String key) {
		return null;
	}

	@Override
	public void saveState(String key, Properties configuration) {
		this.saved = configuration;
	}

	public Properties getSaved() {
		return this.saved;
	}

	@Override
	public Properties loadState(String key) {
		Properties p = new Properties();
		p.put("section1", "1");
		p.put("section2", "26");
		return p;
	}

}
