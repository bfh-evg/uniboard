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

import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.uniboard.service.configuration.State;
import java.io.File;
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
	public Configuration getConfiguration(String key) {

		if (this.correct && this.groupded) {
			Configuration p = new Configuration();
			File f = new File("src/test/resources/numberSchema.json");
			p.getEntries().put("number", f.getAbsolutePath());
			File f2 = new File("src/test/resources/ipSchema.json");
			p.getEntries().put("ip", f2.getAbsolutePath());
			return p;
		}
		if (this.correct && !this.groupded) {
			Configuration p = new Configuration();
			File f = new File("src/test/resources/numberSchema.json");
			p.getEntries().put("singleType", f.getAbsolutePath());
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
	public void saveState(State state) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public <T extends State> T loadState(String key, Class<T> t) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
