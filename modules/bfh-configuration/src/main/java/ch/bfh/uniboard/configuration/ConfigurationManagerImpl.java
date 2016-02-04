/*
 * Uniboard
 *
 *  Copyright (c) 2015 Bern University of Applied Sciences (BFH),
 *  Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniVote2 may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH),
 *   Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: severin.hauser@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.uniboard.configuration;

import static com.mongodb.client.model.Filters.*;
import ch.bfh.uniboard.persistence.mongodb.ConnectionManager;
import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.uniboard.service.configuration.State;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import org.bson.Document;
import org.bson.conversions.Bson;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@Startup
public class ConfigurationManagerImpl implements ConfigurationManager {

	private static final Logger logger = Logger.getLogger(ConfigurationManagerImpl.class.getName());

	@Resource(name = "ConfigurationCollection")
	private String COLLECTION_NAME = "uniboard-configuration";

	public Map<String, Configuration> configurations;

	@EJB
	ConnectionManager connectionManager;

	@PostConstruct
	protected void init() {
		configurations = new HashMap<>();
		Bson query = exists("config_key");
		MongoCursor<Document> cursor
				= this.connectionManager.getCollection(COLLECTION_NAME).find(query, Document.class).iterator();
		while (cursor.hasNext()) {
			try {
				String s = cursor.next().toJson();
				Configuration cfg = ConfigurationManagerImpl.unmarshal(Configuration.class, s);
				this.configurations.put(cfg.getConfig_key(), cfg);
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Could not load configuration.", ex);
			}
		}
		logger.log(Level.INFO, "Loaded configurations.");
	}

	@Override
	public Configuration getConfiguration(String key) {
		return this.configurations.get(key);
	}

	@Override
	public void saveState(State state) {
		try {
			String stateString = ConfigurationManagerImpl.marshal(state);
			System.out.println(stateString);
			Bson query = eq("state_key", state.getKey());
			Document newState = Document.parse(stateString);
			UpdateOptions uOptions = new UpdateOptions();
			uOptions.upsert(true);
			this.connectionManager.getCollection(COLLECTION_NAME).replaceOne(query, newState, uOptions);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public <T extends State> T loadState(String key, Class<T> t) {
		try {
			Bson query = eq("state_key", key);
			Document state = this.connectionManager.getCollection(COLLECTION_NAME).find(query).first();
			if (state != null) {
				return ConfigurationManagerImpl.unmarshal(t, state.toJson());
			}
			return null;
		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
			return null;
		}
	}

	/**
	 * Initializes the JAXB context.
	 *
	 * @param <T> the Java type of the domain class the conversion takes place
	 * @param type the actual type object
	 * @return the JAXB context
	 * @throws Exception if the context cannot be established
	 */
	private static <T> JAXBContext initJAXBContext(Class<T> type) throws Exception {
		Map<String, Object> properties = new HashMap<>();
		properties.put("eclipselink.media-type", "application/json");
		properties.put("eclipselink.json.include-root", false);
		//State class added to ensure that moxy is used
		return JAXBContext.newInstance(new Class<?>[]{State.class, type}, properties);
	}

	/**
	 * Converts a JSON string into the corresponding domain class.
	 *
	 * @param <T> the Java type of the domain class the conversion takes place
	 * @param type the actual type object
	 * @param message a JSON string
	 * @return the Java instance of the domain class
	 * @throws Exception if the conversion cannot be made
	 */
	protected static <T> T unmarshal(Class<T> type, String message) throws Exception {
		JAXBContext jaxbContext = ConfigurationManagerImpl.initJAXBContext(type);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Reader reader = new StringReader(message);
		return unmarshaller.unmarshal(new StreamSource(reader), type).getValue();

	}

	/**
	 * Given an instance of a domain class denoting a JSON object, converts it into a JSON string.
	 *
	 * @param object an instance of a domain class denoting a JSON object
	 * @return a JSON string
	 * @throws Exception if there is an error
	 */
	protected static String marshal(Object object) throws Exception {
		JAXBContext jaxbContext = ConfigurationManagerImpl.initJAXBContext(object.getClass());
		StringWriter writer = new StringWriter();
		Marshaller marshaller = jaxbContext.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
		marshaller.marshal(object, writer);
		return writer.toString();
	}

}
