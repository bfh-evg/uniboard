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
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.bson.Document;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@Startup
@LocalBean
public class ConnectionManagerTestImpl implements ConnectionManager {

	private static final Logger logger = Logger.getLogger(ConnectionManagerImpl.class.getName());

	//TODO put this in a config file
	private static final String host = "localhost";
	private static final String dbName = "testDB";
	private static final String collectionName = "test";
	protected static final int port = 27017;
	private static final String username = "test";
	private static final String password = "test";
	//must be false in Unit testing config
	private static final boolean authentication = false;

	private MongoCollection<Document> collection;
	private MongoClient mongoClient;
	private boolean connected = false;

	private static final MongodStarter starter = MongodStarter.getDefaultInstance();
	private static MongodExecutable mongodExe;
	private static MongodProcess mongod;

	@PostConstruct
	private void init() {

		try {
			mongodExe = starter.prepare(new MongodConfigBuilder()
					.version(Version.Main.PRODUCTION)
					.net(new Net(27017, Network.localhostIsIPv6()))
					.build());
			mongod = mongodExe.start();
			mongoClient = new MongoClient(host, port);
			this.connected = true;
		} catch (UnknownHostException ex) {
			logger.log(Level.SEVERE, "DB creation error", ex);
			return;
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "DB creation error", ex);
			return;
		}

		//Create or load the database
		MongoDatabase db = mongoClient.getDatabase(dbName);

		for (final String name : db.listCollectionNames()) {
			if (name.equalsIgnoreCase(collectionName)) {
				//load the collection
				collection = db.getCollection(collectionName);
				return;
			}
		}
		//create the collection if it does not exist
		db.createCollection(collectionName);
		collection = db.getCollection(collectionName);
	}

	@PreDestroy
	private void preDestroy() {
		mongoClient.close();
	}

	@Override
	public MongoCollection<Document> getCollection(String collectionName) {
		return this.collection;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public void createCollection(String collectionName) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
