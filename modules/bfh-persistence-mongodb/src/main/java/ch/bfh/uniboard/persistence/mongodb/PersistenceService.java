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

import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.Post;
import ch.bfh.uniboard.service.data.ResultContainer;
import ch.bfh.uniboard.service.data.Constraint;
import ch.bfh.uniboard.service.*;
import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.Between;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.uniboard.service.data.Equal;
import ch.bfh.uniboard.service.data.Greater;
import ch.bfh.uniboard.service.data.GreaterEqual;
import ch.bfh.uniboard.service.data.In;
import ch.bfh.uniboard.service.data.Less;
import ch.bfh.uniboard.service.data.LessEqual;
import ch.bfh.uniboard.service.data.MessageIdentifier;
import ch.bfh.uniboard.service.data.NotEqual;
import ch.bfh.uniboard.service.data.Order;
import ch.bfh.uniboard.service.data.PropertyIdentifier;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.bson.Document;
import org.bson.conversions.Bson;

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
	private String DEFAULT_COLLECTION = "uniboard";

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
				betaError.add(new Attribute(Attributes.ERROR, "Internal Server Error. Service not available"));
				logger.log(Level.WARNING, "Database error: unable to connect to database");
				return betaError;
			}

			PersistedPost pp = new PersistedPost();
			pp.setMessage(message);
			pp.setAlpha(alpha);
			pp.setBeta(beta);

			MongoCollection collection = this.connectionManager.getCollection(DEFAULT_COLLECTION);
			if (collection == null) {
				Attributes betaError = new Attributes();
				betaError.add(new Attribute(Attributes.ERROR, "Internal Server Error. Service not available"));
				logger.log(Level.WARNING, "Collection not found: {0}", DEFAULT_COLLECTION);
				return betaError;
			}

			collection.insertOne(pp.toDocument());

			return beta;
		} catch (Exception e) {
			Attributes betaError = new Attributes();
			betaError.add(new Attribute(Attributes.ERROR, "Internal Server Error. Service not available"));
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
				gamma.add(new Attribute(Attributes.ERROR, "Internal Server Error. Service not available"));
				logger.log(Level.WARNING, "Database error: unable to connect to database");
				return new ResultContainer(new ArrayList<Post>(), gamma);
			}

			List<Bson> constraintsList = new ArrayList<>();

			//iterates over the constraints and constructs the corresponding query string
			for (Constraint c : query.getConstraints()) {

				//constructs the key
				String keyString = "";
				if (c.getIdentifier() instanceof MessageIdentifier) {
					MessageIdentifier tmp = (MessageIdentifier) c.getIdentifier();
					keyString += "searchable-message";
					keyString += "." + tmp.getKeyPath();
				} else if (c.getIdentifier() instanceof PropertyIdentifier) {
					PropertyIdentifier tmp = (PropertyIdentifier) c.getIdentifier();
					keyString += tmp.getType().value();
					keyString += "." + tmp.getKeyPath();
				} else {
					Attributes gamma = new Attributes();
					gamma.add(new Attribute(Attributes.REJECTED, "Syntax error: Unknown identifier"));
					logger.log(Level.WARNING, "Syntax error: Unknown identifier");
					return new ResultContainer(new ArrayList<Post>(), gamma);
				}

				DataType dt;
				if (c.getDataType() != null) {
					dt = c.getDataType();
				} else {
					dt = DataType.STRING;
				}

				if (c instanceof Equal) {
					Equal op = (Equal) c;
					constraintsList.add(Filters.eq(keyString, castValue(op.getValue(), dt)));
				} else if (c instanceof NotEqual) {
					NotEqual op = (NotEqual) c;
					constraintsList.add(Filters.ne(keyString, castValue(op.getValue(), dt)));
				} else if (c instanceof In) {
					In op = (In) c;
					List<Object> values = new ArrayList<>();
					for (String in : op.getSet()) {
						values.add(this.castValue(in, dt));
					}
					constraintsList.add(Filters.in(keyString, values));
				} else if (c instanceof Between) {
					Between op = (Between) c;
					constraintsList.add(Filters.gt(keyString, castValue(op.getLowerBound(), dt)));
					constraintsList.add(Filters.lt(keyString, castValue(op.getUpperBound(), dt)));
				} else if (c instanceof Greater) {
					Greater op = (Greater) c;
					constraintsList.add(Filters.gt(keyString, castValue(op.getValue(), dt)));
				} else if (c instanceof GreaterEqual) {
					GreaterEqual op = (GreaterEqual) c;
					constraintsList.add(Filters.gte(keyString, castValue(op.getValue(), dt)));
				} else if (c instanceof Less) {
					Less op = (Less) c;
					constraintsList.add(Filters.lt(keyString, castValue(op.getValue(), dt)));
				} else if (c instanceof LessEqual) {
					LessEqual op = (LessEqual) c;
					constraintsList.add(Filters.lte(keyString, castValue(op.getValue(), dt)));
				} else {
					Attributes gamma = new Attributes();
					gamma.add(new Attribute(Attributes.REJECTED, "Syntax error: Unknown type of constraint"));
					logger.log(Level.WARNING, "Syntax error: Unknown type of constraint");
					return new ResultContainer(new ArrayList<Post>(), gamma);
				}
			}
			Bson constraints = Filters.and(constraintsList);

			List<Bson> ordersList = new ArrayList<>();
			for (Order order : query.getOrder()) {
				String identifier;
				if (order.getIdentifier() instanceof MessageIdentifier) {
					MessageIdentifier tmp = (MessageIdentifier) order.getIdentifier();
					identifier = "searchable-message";
					identifier += "." + tmp.getKeyPath();
				} else if (order.getIdentifier() instanceof PropertyIdentifier) {
					PropertyIdentifier tmp = (PropertyIdentifier) order.getIdentifier();
					identifier = tmp.getType().value();
					identifier += "." + tmp.getKeyPath();
				} else {
					Attributes gamma = new Attributes();
					gamma.add(new Attribute(Attributes.REJECTED, "Syntax error: Unknown identifier"));
					logger.log(Level.WARNING, "Syntax error: Unknown identifier");
					return new ResultContainer(new ArrayList<Post>(), gamma);
				}
				if (order.isAscDesc()) {
					ordersList.add(Sorts.ascending(identifier));
				} else {
					ordersList.add(Sorts.descending(identifier));
				}
			}
			Bson order = Sorts.orderBy(ordersList);
			FindIterable<Document> documents = this.connectionManager.getCollection(DEFAULT_COLLECTION)
					.find(constraints).sort(order).limit(query.getLimit());
			//creates the result container with the db result
			List<Post> list = new ArrayList<>();
			MongoCursor<Document> cursor = documents.iterator();
			while (cursor.hasNext()) {
				Document object = cursor.next();
				//convert to PersistedPost
				list.add(PersistedPost.fromDocument(object));
			}
			return new ResultContainer(list, new Attributes());
		} catch (ParseException e) {
			Attributes gamma = new Attributes();
			gamma.add(new Attribute(Attributes.ERROR, "Could not parse constraint: " + e.getMessage()));
			logger.log(Level.SEVERE, "Could not parse constraint.", e);
			return new ResultContainer(new ArrayList<Post>(), gamma);
		}
	}

	private Object castValue(String value, DataType dataType) throws ParseException {
		switch (dataType) {
			case STRING:
				return value;
			case INTEGER:
				return Integer.parseInt(value);
			case DATE:
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
				dateFormat.setTimeZone(timeZone);
				return dateFormat.parse(value);
			default:
				return value;
		}
	}
}
