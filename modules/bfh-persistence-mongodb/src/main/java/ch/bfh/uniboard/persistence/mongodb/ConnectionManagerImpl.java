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
package ch.bfh.uniboard.persistence.mongodb;

import ch.bfh.uniboard.service.ConfigurationManager;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@Startup
@LocalBean
public class ConnectionManagerImpl implements ConnectionManager {

	private static final Logger logger = Logger.getLogger(ConnectionManagerImpl.class.getName());

	private static final String CONFIG_NAME = "bfh-mongodb";
	private static final String HOST_KEY = "host";
	private static final String DBNAME_KEY = "dbname";
	private static final String COLLECTION_KEY = "collection";
	private static final String PORT_KEY = "port";
	private static final String USERNAME_KEY = "username";
	private static final String PASSWORD_KEY = "password";
	private static final String AUTH_KEY = "authentication";

	private DBCollection collection;
	private MongoClient mongoClient;
	private boolean connected = false;

	@EJB
	ConfigurationManager cm;

	@PostConstruct
	private void init() {

		Properties props = cm.getConfiguration(CONFIG_NAME);

		if (props == null) {
			logger.log(Level.SEVERE, "Confiugration could not be loaded.");
			return;
		}
		//DB Connection Information
		String host;
		String dbName;
		String collectionName;
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
		if (props.containsKey(COLLECTION_KEY)) {
			collectionName = props.getProperty(COLLECTION_KEY);
		} else {
			collectionName = "default";
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

		try {
			if (authentication) {
				MongoCredential credential = MongoCredential.createMongoCRCredential(username, dbName, password.toCharArray());
				//MongoClient already works as a pool if only one instance is used (http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/)
				mongoClient = new MongoClient(new ServerAddress(host, port), Arrays.asList(credential));
			} else {
				mongoClient = new MongoClient(host, port);
			}
			this.connected = true;
		} catch (UnknownHostException ex) {
			logger.log(Level.SEVERE, "DB creation error", ex);
			return;
		}

		//Create or load the database
		DB db = mongoClient.getDB(dbName);

		//create the collection if it does not exist
		if (!db.collectionExists(collectionName)) {
			collection = db.createCollection(collectionName, null);
		}
		//load the collection
		collection = db.getCollection(collectionName);
	}

	@PreDestroy
	private void preDestroy() {
		mongoClient.close();
	}

	@Override
	public DBCollection getCollection() {
		return this.collection;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

}
