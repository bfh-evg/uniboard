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
package ch.bfh.uniboard.persistence.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.NamingException;
import org.bson.Document;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@Startup
@LocalBean
public class ConnectionManagerImpl implements ConnectionManager {

	private static final Logger logger = Logger.getLogger(ConnectionManagerImpl.class.getName());

	@Resource(name = "JNDI_URI")
	private String JNDI_URI;

	private static final String HOST_KEY = "host";
	private static final String DBNAME_KEY = "dbname";
	private static final String PORT_KEY = "port";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String AUTH_KEY = "authentication";

	private MongoDatabase db;
	private MongoClient mongoClient;
	private boolean connected = false;

	@PostConstruct
	private void init() {

		if (JNDI_URI == null) {
			JNDI_URI = "uniboard/mongodb-connector";
		}

		Properties props;
		try {
			javax.naming.InitialContext ic = new javax.naming.InitialContext();
			props = (Properties) ic.lookup(JNDI_URI);
		} catch (NamingException ex) {
			logger.log(Level.SEVERE, "JNDI lookup for " + JNDI_URI + " failed."
					+ "ConfigurationManager could not be initialized. Exception: {0}",
					new Object[]{ex});
			return;
		}

		if (props == null) {
			logger.log(Level.SEVERE, "Configuration could not be loaded.");
			return;
		}
		//DB Connection Information
		String host;
		String dbName;
		int port;
		String username;
		String password;
		boolean authentication;
		//Check if values are set or use defaults
		if (props.containsKey(HOST_KEY)) {
			host = props.getProperty(HOST_KEY);
		} else {
			host = "localhost";
		}
		if (props.containsKey(DBNAME_KEY)) {
			dbName = props.getProperty(DBNAME_KEY);
		} else {
			dbName = "uniboard";
		}
		if (props.containsKey(PORT_KEY)) {
			port = Integer.parseInt(props.getProperty(PORT_KEY));
		} else {
			port = 27017;
		}
		if (props.containsKey(USERNAME_KEY)) {
			username = props.getProperty(USERNAME_KEY);
		} else {
			username = "admin";
		}
		if (props.containsKey(PASSWORD_KEY)) {
			password = props.getProperty(PASSWORD_KEY);
		} else {
			password = "password";
		}
		if (props.containsKey(AUTH_KEY)) {
			authentication = Boolean.parseBoolean(props.getProperty(AUTH_KEY));
		} else {
			authentication = false;
		}

		if (authentication) {
			MongoCredential credential
					= MongoCredential.createMongoCRCredential(username, dbName, password.toCharArray());
			//MongoClient already works as a pool if only one instance is used (http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/)
			mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
		} else {
			mongoClient = new MongoClient(host, port);
		}
		this.connected = true;
		//Create or load the database
		db = mongoClient.getDatabase(dbName);

	}

	@PreDestroy
	private void preDestroy() {
		mongoClient.close();
	}

	@Override
	public MongoCollection<Document> getCollection(String collectionName) {

		for (final String name : db.listCollectionNames()) {
			if (name.equalsIgnoreCase(collectionName)) {
				//load the collection
				return db.getCollection(collectionName);
			}
		}
		//return null if the collection does not exist
		return null;

	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void createCollection(String collectionName) {
		db.createCollection(collectionName);
	}

}
