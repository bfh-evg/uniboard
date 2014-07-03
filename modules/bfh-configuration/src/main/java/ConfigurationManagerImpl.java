
import ch.bfh.uniboard.service.ConfigurationService;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project Univote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
public class ConfigurationManagerImpl implements ConfigurationService {

	public Map<String, Properties> configurations;

	@PostConstruct
	public void init() {
		configurations = new HashMap<>();
		//TODO load configurations from somewhere
	}

	@Override
	public Properties getConfiguration(String key) {
		return this.configurations.get(key);
	}

}
