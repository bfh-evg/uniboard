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

import ch.bfh.uniboard.service.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Service responsible for persisting the received posts
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class PersistenceService implements PostService, GetService {

	private static final Logger logger = Logger.getLogger(PersistenceService.class.getName());

	private static final String SECTION = "section";
	@Resource(name = "collection")
	protected static final String DEFAULT_COLLECTION = "uniboard";

	@EJB
	ConnectionManager connectionManager;

	/**
	 * Creates the persistence component
	 */
	public PersistenceService() {
	}

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		try {
			//Check Database connection
			if (!this.connectionManager.isConnected()) {
				Attributes betaError = new Attributes();
				betaError.add(Attributes.ERROR, new StringValue("Internal Server Error. Service not available"));
				logger.log(Level.WARNING, "Database error: unable to connect to database");
				return betaError;
			}

			PersistedPost pp = new PersistedPost();
			pp.setMessage(message);
			pp.setAlpha(alpha);
			pp.setBeta(beta);

			DBCollection collection = this.connectionManager.getCollection(DEFAULT_COLLECTION);
			if (collection == null) {
				Attributes betaError = new Attributes();
				betaError.add(Attributes.ERROR, new StringValue("Internal Server Error. Service not available"));
				logger.log(Level.WARNING, "Collection not found: " + DEFAULT_COLLECTION);
				return betaError;
			}

			collection.insert(pp.toDBObject());

			return beta;
		} catch (Exception e) {
			Attributes betaError = new Attributes();
			betaError.add(Attributes.ERROR, new StringValue("Internal Server Error. Service not available"));
			logger.log(Level.WARNING, "General post error", e);
			return betaError;
		}
	}

	@Override
	public ResultContainer get(Query query) {
		try {
			//Check Database connection
			if (!this.connectionManager.isConnected()) {
				Attributes gamma = new Attributes();
				gamma.add(Attributes.ERROR, new StringValue("Internal Server Error. Service not available"));
				logger.log(Level.WARNING, "Database error: unable to connect to database");
				return new ResultContainer(new ArrayList<Post>(), gamma);
			}

			List<DBObject> constraintsList = new ArrayList<>();

			//iterates over the constraints and constructs the corresponding query string
			for (Constraint c : query.getConstraints()) {

				//constructs the key
				String keyString = "";
				if (c.getIdentifier() instanceof MessageIdentifier) {
					//TODO constraint that wants to compare the raw byte[]
					keyString += "searchable-message";
				} else if (c.getIdentifier() instanceof AlphaIdentifier) {
					keyString += "alpha";
				} else if (c.getIdentifier() instanceof BetaIdentifier) {
					keyString += "beta";
				} else {
					Attributes gamma = new Attributes();
					gamma.add(Attributes.REJECTED, new StringValue("Syntax error: Unknown identifier"));
					logger.log(Level.WARNING, "Syntax error: Unknown identifier");
					return new ResultContainer(new ArrayList<Post>(), gamma);
				}

				//constructs the hierarchy of the keys
				for (String key : c.getIdentifier().getParts()) {
					keyString += "." + key;
				}

				//constructs the researched value string by checking the type of constraints and getting the searched values
				DBObject actualConstraint = new BasicDBObject();
				if (c instanceof Equal) {
					Equal op = (Equal) c;
					actualConstraint.put(keyString, op.getValue().getValue());
				} else if (c instanceof NotEqual) {
					NotEqual op = (NotEqual) c;
					actualConstraint.put(keyString, new BasicDBObject("$ne", op.getValue().getValue()));
				} else if (c instanceof In) {
					In op = (In) c;
					List<Object> values = new ArrayList<>();
					Class valueClass = op.getSet().get(0).getClass();
					for (Value v : op.getSet()) {
						if (!(v.getClass().equals(valueClass))) {
							Attributes gamma = new Attributes();
							gamma.add(Attributes.REJECTED,
									new StringValue("Syntax error: not same value type for IN constraint"));
							logger.log(Level.WARNING, "Syntax error: not same value type for IN constraint");
							return new ResultContainer(new ArrayList<Post>(), gamma);
						}
						values.add(v.getValue());
					}
					actualConstraint.put(keyString, new BasicDBObject("$in", values));
				} else if (c instanceof Between) {
					Between op = (Between) c;
					if (!(op.getStart().getClass().equals(op.getEnd().getClass()))) {
						Attributes gamma = new Attributes();
						gamma.add(Attributes.REJECTED,
								new StringValue("Syntax error: not same value type for BETWEEN constraint"));
						logger.log(Level.WARNING, "Syntax error: not same value type for BETWEEN constraint");
						return new ResultContainer(new ArrayList<Post>(), gamma);
					}
					actualConstraint.put(keyString,
							new BasicDBObject("$gt", op.getStart().getValue()).append("$lt", op.getEnd().getValue()));
				} else if (c instanceof Greater) {
					Greater op = (Greater) c;
					actualConstraint.put(keyString, new BasicDBObject("$gt", op.getValue().getValue()));
				} else if (c instanceof GreaterEqual) {
					GreaterEqual op = (GreaterEqual) c;
					actualConstraint.put(keyString, new BasicDBObject("$gte", op.getValue().getValue()));
				} else if (c instanceof Less) {
					Less op = (Less) c;
					actualConstraint.put(keyString, new BasicDBObject("$lt", op.getValue().getValue()));
				} else if (c instanceof LessEqual) {
					LessEqual op = (LessEqual) c;
					actualConstraint.put(keyString, new BasicDBObject("$lte", op.getValue().getValue()));
				} else {
					Attributes gamma = new Attributes();
					gamma.add(Attributes.REJECTED, new StringValue("Syntax error: Unknown type of constraint"));
					logger.log(Level.WARNING, "Syntax error: Unknown type of constraint");
					return new ResultContainer(new ArrayList<Post>(), gamma);
				}
				constraintsList.add(actualConstraint);
			}

			//combine the different constrainst in an AND query
			DBObject completeQuery = new BasicDBObject();
			completeQuery.put("$and", constraintsList);

			DBCursor cursor;

			if (query.getOrder().size() > 0) {

				//Create orderBy
				BasicDBObject orderBy = new BasicDBObject();

				for (Order order : query.getOrder()) {
					String identifier;
					if (order.getIdentifier() instanceof MessageIdentifier) {
						identifier = "message";
					} else if (order.getIdentifier() instanceof AlphaIdentifier) {
						identifier = "alpha";
					} else if (order.getIdentifier() instanceof BetaIdentifier) {
						identifier = "beta";
					} else {
						Attributes gamma = new Attributes();
						gamma.add(Attributes.REJECTED, new StringValue("Syntax error: Unknown identifier"));
						logger.log(Level.WARNING, "Syntax error: Unknown identifier");
						return new ResultContainer(new ArrayList<Post>(), gamma);
					}
					for (String key : order.getIdentifier().getParts()) {
						identifier += "." + key;
					}
					int ascDesc;
					if (order.isAscDesc()) {
						ascDesc = 1;
					} else {
						ascDesc = -1;
					}
					orderBy.append(identifier, ascDesc);
				}

				cursor = this.connectionManager.getCollection(DEFAULT_COLLECTION)
						.find(completeQuery).sort(orderBy).limit(query.getLimit());
			} else {
				//apply query on database
				cursor = this.connectionManager.getCollection(DEFAULT_COLLECTION).
						find(completeQuery).limit(query.getLimit());
			}

			//creates the result container with the db result
			List<Post> list = new ArrayList<>();
			while (cursor.hasNext()) {
				DBObject object = cursor.next();
				//convert to PersistedPost
				list.add(PersistedPost.fromDBObject(object));
			}
			return new ResultContainer(list, new Attributes());
		} catch (Exception e) {
			Attributes gamma = new Attributes();
			gamma.add(Attributes.ERROR, new StringValue("General error: " + e.getMessage()));
			logger.log(Level.WARNING, "General Get error", e);
			return new ResultContainer(new ArrayList<Post>(), gamma);
		}
	}

}
