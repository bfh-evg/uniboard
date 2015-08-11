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
import java.io.File;
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
			File f = new File("src/test/resources/numberSchema.json");
			p.put("number", f.getAbsolutePath());
			File f2 = new File("src/test/resources/ipSchema.json");
			p.put("ip", f2.getAbsolutePath());
			return p;
		}
		if (this.correct && !this.groupded) {
			Properties p = new Properties();
			File f = new File("src/test/resources/numberSchema.json");
			p.put("singleType", f.getAbsolutePath());
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

	@Override
	public void saveState(String key, Properties configuration) {
	}

	@Override
	public Properties loadState(String key) {
		return null;
	}

}
