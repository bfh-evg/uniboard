/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.configuration;

import ch.bfh.uniboard.service.ConfigurationManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.naming.NamingException;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
public class ConfigurationManagerImpl implements ConfigurationManager {

	private static final Logger logger = Logger.getLogger(ConfigurationManagerImpl.class.getName());

	public Map<String, Properties> configurations;

	@PostConstruct
	public void init() {
		configurations = new HashMap<>();
		Properties props;
		try {
			javax.naming.InitialContext ic = new javax.naming.InitialContext();
			props = (Properties) ic.lookup("/uniboard/configuration");
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, "JNDI lookup for '/uniboard/configuration' failed."
					+ "ConfigurationManager could not be initialized. Exception: {0}",
					new Object[]{ex});
			return;
		}
		for (String componentKey : props.stringPropertyNames()) {
			String[] split = componentKey.split("\\.");
			String type = split[split.length - 1];
			String key = split[0];

			switch (type) {
				case "external":
					try {
						InputStream in = new FileInputStream(props.getProperty(componentKey));
						Properties tmpProperties = new Properties();
						tmpProperties.load(in);
						this.configurations.put(key, tmpProperties);
					} catch (IOException ex) {
						logger.log(Level.WARNING, "File not found {0}",
								new Object[]{props.getProperty(componentKey), ex});
					}

					break;
				case "jndi":
					Properties tmpProperties;
					try {
						javax.naming.InitialContext ic = new javax.naming.InitialContext();
						tmpProperties = (Properties) ic.lookup(props.getProperty(componentKey));
						this.configurations.put(key, tmpProperties);
					} catch (NamingException ex) {
						logger.log(Level.WARNING, "JNDI lookup for '{0}' failed. Exception: {1}",
								new Object[]{props.getProperty(componentKey), ex});
					}
					break;
				default:
					logger.log(Level.WARNING, "Unsupported resource type: {0}", new Object[]{type});
					break;
			}

		}
	}

	@Override
	public Properties getConfiguration(String key) {
		return this.configurations.get(key);
	}

}
