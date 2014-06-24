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
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equals;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEquals;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.InternalGet;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEquals;
import ch.bfh.uniboard.service.NotEquals;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service responsible for persisting the received posts
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class PersistenceService implements PostService, GetService, InternalGet {
    
    private static final Logger logger = Logger.getLogger(PersistenceService.class.getName());

    private DBCollection collection;

    /**
     * Creates the persistence component
     */
    public PersistenceService() {
        MongoClient mongoClient = null;
        try {
            //MongoClient already works as a pool if only one instance is used (http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/)
            mongoClient = new MongoClient();
        } catch (UnknownHostException ex) {
            logger.log(Level.SEVERE, "DB creation error", ex);
            return;
        }

        //Create or load the database
        DB db = mongoClient.getDB("testDB");

        //authenticates the user
        //boolean auth = db.authenticate(myUserName, myPassword);
        //create or load the collection
        collection = db.getCollection("test");
        if (collection == null) {
            collection = db.createCollection("test", null);
        }
    }
    
    @Override
    public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
        try {
            //Check Database connection
            if (collection == null) {
                Attributes betaError = new Attributes();
                betaError.add(Attributes.ERROR, new StringValue("Database error: unable to connect to database"));
                logger.log(Level.WARNING, "Database error: unable to connect to database");
                return betaError;
            }

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
            
            this.collection.insert(pp.toDBObject());
            
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
            if (collection == null) {
                Attributes gamma = new Attributes();
                gamma.add(Attributes.ERROR, new StringValue("Database error: unable to connect to database"));
                logger.log(Level.WARNING, "Database error: unable to connect to database");
                return new ResultContainer(new TreeSet<Post>(), gamma);
            }

            //check basic wellformedness of query
            if (query == null || query.getConstraints() == null || query.getConstraints().isEmpty() || query.getConstraints().contains(null)) {
                Attributes gamma = new Attributes();
                gamma.add(Attributes.REJECTED, new StringValue("Syntax error: Incomplete query"));
                logger.log(Level.WARNING, "Syntax error: Incomplete query");
                return new ResultContainer(new TreeSet<Post>(), gamma);
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
                        return new ResultContainer(new TreeSet<Post>(), gamma);
                }

                //constructs the hierarchy of the keys
                for (String key : c.getKeys()) {
                    keyString += "." + key;
                }

                //constructs the researched value string by checking the type of constraints and getting the searched values 
                DBObject actualConstraint = new BasicDBObject();
                if (c instanceof Equals) {
                    Equals op = (Equals) c;
                    actualConstraint.put(keyString, op.getValue().getValue());
                } else if (c instanceof NotEquals) {
                    NotEquals op = (NotEquals) c;
                    actualConstraint.put(keyString, new BasicDBObject("$ne", op.getValue().getValue()));
                } else if (c instanceof In) {
                    In op = (In) c;
                    List<Object> values = new ArrayList<>();
                    Class valueClass = op.getSet().get(0).getClass();
                    for (Value v : op.getSet()) {
                        if (!(v.getClass().equals(valueClass))) {
                            //TODO test it
                            Attributes gamma = new Attributes();
                            gamma.add(Attributes.REJECTED, new StringValue("Syntax error: not same value type for IN constraint"));
                            logger.log(Level.WARNING, "Syntax error: not same value type for IN constraint");
                            return new ResultContainer(new TreeSet<Post>(), gamma);
                        }
                        values.add(v.getValue());
                    }
                    actualConstraint.put(keyString, new BasicDBObject("$in", values));
                } else if (c instanceof Between) {
                    Between op = (Between) c;
                    if (!(op.getStart().getClass().equals(op.getEnd().getClass()))) {
                        //TODO test it
                        Attributes gamma = new Attributes();
                        gamma.add(Attributes.REJECTED, new StringValue("Syntax error: not same value type for BETWEEN constraint"));
                        logger.log(Level.WARNING, "Syntax error: not same value type for BETWEEN constraint");
                        return new ResultContainer(new TreeSet<Post>(), gamma);
                    }
                    actualConstraint.put(keyString, new BasicDBObject("$gt", op.getStart().getValue()).append("$lt", op.getEnd().getValue()));
                } else if (c instanceof Greater) {
                    Greater op = (Greater) c;
                    actualConstraint.put(keyString, new BasicDBObject("$gt", op.getValue().getValue()));
                } else if (c instanceof GreaterEquals) {
                    GreaterEquals op = (GreaterEquals) c;
                    actualConstraint.put(keyString, new BasicDBObject("$gte", op.getValue().getValue()));
                } else if (c instanceof Less) {
                    Less op = (Less) c;
                    actualConstraint.put(keyString, new BasicDBObject("$lt", op.getValue().getValue()));
                } else if (c instanceof LessEquals) {
                    LessEquals op = (LessEquals) c;
                    actualConstraint.put(keyString, new BasicDBObject("$lte", op.getValue().getValue()));
                } else {
                    Attributes gamma = new Attributes();
                    gamma.add(Attributes.REJECTED, new StringValue("Syntax error: Unknown type of constraint"));
                    logger.log(Level.WARNING, "Syntax error: Unknown type of constraint");
                    return new ResultContainer(new TreeSet<Post>(), gamma);
                }
                constraintsList.add(actualConstraint);
            }

            //combine the different constrainst in an AND query
            DBObject completeQuery = new BasicDBObject();
            completeQuery.put("$and", constraintsList);

            //apply query on database
            DBCursor cursor = this.collection.find(completeQuery);

            //creates the result container with the db result
            SortedSet<Post> ss = new TreeSet<>();
            while (cursor.hasNext()) {
                DBObject object = cursor.next();
                //convert to PersistedPost
                ss.add(PersistedPost.fromDBObject(object));
            }
            return new ResultContainer(ss, new Attributes());
        } catch (Exception e) {
            Attributes gamma = new Attributes();
            gamma.add(Attributes.ERROR, new StringValue("General error: " + e.getMessage()));
            logger.log(Level.WARNING, "General Get error", e);
            return new ResultContainer(new TreeSet(), gamma);
        }
    }
    
    @Override
    public SortedSet<Post> internalGet(Query q) {
        return this.get(q).getResult();
    }

    /**
     * Make the collection of the database available for test
     *
     * @return the collection of the database
     */
    protected DBCollection getCollection() {
        return this.collection;
    }
}
