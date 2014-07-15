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

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Between;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEqual;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEqual;
import ch.bfh.uniboard.service.NotEqual;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Service responsible for persisting the received posts
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
@Stateless
public class PersistenceService implements PostService, GetService {

	private static final Logger logger = Logger.getLogger(PersistenceService.class.getName());

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

			//TODO Remove this block and its tests
			//check basic wellformedness of post
			if (message == null || alpha == null || beta == null) {
				Attributes betaError = new Attributes();
				betaError.add(Attributes.REJECTED, new StringValue("Syntax error: incomplete post"));
				logger.log(Level.WARNING, "Syntax error: incomplete post");
				return betaError;
			}

			PersistedPost pp = new PersistedPost();
			pp.setMessage(message);
			pp.setAlpha(alpha);
			pp.setBeta(beta);

			this.connectionManager.getCollection().insert(pp.toDBObject());

			return beta;
		} catch (Exception e) {
			Attributes betaError = new Attributes();
			betaError.add(Attributes.ERROR, new StringValue("General post error: " + e.getMessage()));
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

			//TODO Remove this block and its tests
			//check basic wellformedness of query
			if (query == null || query.getConstraints() == null || query.getConstraints().isEmpty() || query.getConstraints().contains(null)) {
				Attributes gamma = new Attributes();
				gamma.add(Attributes.REJECTED, new StringValue("Syntax error: Incomplete query"));
				logger.log(Level.WARNING, "Syntax error: Incomplete query");
				return new ResultContainer(new ArrayList<Post>(), gamma);
			}

			List<DBObject> constraintsList = new ArrayList<>();

			//iterates over the constraints and constructs the corresponding query string
			for (Constraint c : query.getConstraints()) {

				//constructs the key
				String keyString = "";
				switch (c.getPostElement()) {
					case MESSAGE:
						keyString += "message";
						break;
					case ALPHA:
						keyString += "alpha";
						break;
					case BETA:
						keyString += "beta";
						break;
					default:
						Attributes gamma = new Attributes();
						gamma.add(Attributes.REJECTED, new StringValue("Syntax error: Unknown post element"));
						logger.log(Level.WARNING, "Syntax error: Unknown post element");
						return new ResultContainer(new ArrayList<Post>(), gamma);
				}

				//constructs the hierarchy of the keys
				for (String key : c.getKeys()) {
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
							gamma.add(Attributes.REJECTED, new StringValue("Syntax error: not same value type for IN constraint"));
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
						gamma.add(Attributes.REJECTED, new StringValue("Syntax error: not same value type for BETWEEN constraint"));
						logger.log(Level.WARNING, "Syntax error: not same value type for BETWEEN constraint");
						return new ResultContainer(new ArrayList<Post>(), gamma);
					}
					actualConstraint.put(keyString, new BasicDBObject("$gt", op.getStart().getValue()).append("$lt", op.getEnd().getValue()));
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

			//apply query on database
			DBCursor cursor = this.connectionManager.getCollection().find(completeQuery);

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
