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
package ch.bfh.uniboard.typed;

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
	private boolean groupded = true;

	@Override
	public Properties getConfiguration(String key) {

		if (this.correct && this.groupded) {
			Properties p = new Properties();
			p.put("number", "{\"type\" : \"number\"}");
			p.put("ip", "{\"$schema\" : \"http://json-schema.org/draft-03/schema#\",\"format\" : \"ip-address\"}");
			return p;
		}
		if (this.correct && !this.groupded) {
			Properties p = new Properties();
			p.put("singleType", "{\"type\" : \"number\"}");
			return p;
		}
		return null;
	}

	public void setCorrect(boolean correct) {
		this.correct = correct;
	}

	public void setGroupded(boolean groupded) {
		this.groupded = groupded;
	}

}
