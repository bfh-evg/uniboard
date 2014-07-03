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

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
public class ConnectionManager {

	private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());

	//TODO put this in a config file
	private static final String host = "localhost";
	private static final String dbName = "testDB";
	private static final String collectionName = "test";
	protected static final int port = 27017;
	private static final String username = "test";
	private static final String password = "test";
	//must be false in Unit testing config
	private static final boolean authentication = false;

	private DBCollection collection;
	private MongoClient mongoClient;
	private boolean connected = false;

	@PostConstruct
	private void init() {

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

	public DBCollection getCollection() {
		return this.collection;
	}

	public boolean isConnected() {
		return connected;
	}

}
