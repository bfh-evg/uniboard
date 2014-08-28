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
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.DoubleValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A persisted post represents the posted message and all belonging attributes in the format in which it is persisted
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class PersistedPost extends Post {

	/**
	 * Create a persisted post
	 *
	 * @param message
	 * @param alpha
	 * @param beta
	 */
	public PersistedPost(byte[] message, Attributes alpha, Attributes beta) {
		this.message = message;
		this.alpha = alpha;
		this.beta = beta;
	}

	;

    /**
     * Creates an empty persisted post
     */
    public PersistedPost() {
	}

	;

    /**
     * Method allowing to convert the current PersistedPost to the format supported by the database
     * @return a DBObject format of the PersistedPost
     */
    public BasicDBObject toDBObject() {
		BasicDBObject doc = new BasicDBObject();

		//Check if message is a JSON message
		DBObject jsonMessageContent = null;
		try {
			jsonMessageContent = (DBObject) JSON.parse(new String(message, "UTF-8"));
		} catch (JSONParseException | UnsupportedEncodingException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Message is not a JSON string {0}", ex.getMessage());
		}

		if (jsonMessageContent != null) {
			//save message as JSON content
			DBObject jsonMessage = new BasicDBObject("message", jsonMessageContent);
			doc.putAll(jsonMessage);
		} else {
			//save message as byte[]
			doc.put("message", message);
		}

		//Prepares the Alpha attributes
		BasicDBObject alphaList = new BasicDBObject();
		for (Entry<String, Value> entry : alpha.getEntries()) {
			alphaList.put(entry.getKey(), entry.getValue().getValue());
		}
		doc.put("alpha", alphaList);

		//Prepares the Beta attributes
		BasicDBObject betaList = new BasicDBObject();
		for (Entry<String, Value> entry : beta.getEntries()) {
			betaList.put(entry.getKey(), entry.getValue().getValue());
		}
		doc.put("beta", betaList);

		return doc;
	}

	/**
	 * Method allowing to retrieve a PersistedPost out of the DBObject returned by the database. If the passed DBObject
	 * does not represent a PersistedPost, an empty PersistedPost is retuned
	 *
	 * @param doc the DBObject returned by the database
	 * @return the corresponding persisted post
	 */
	public static PersistedPost fromDBObject(DBObject doc) {
		PersistedPost pp = new PersistedPost();

		//Check if message is a JSON message
		if (doc.get("message") instanceof BasicDBObject) {
			//is a JSON string
			try {
				pp.message = JSON.serialize(doc.get("message")).getBytes("UTF-8");
			} catch (UnsupportedEncodingException ex) {
				Logger.getLogger(PersistedPost.class.getName()).log(Level.SEVERE, null, ex);
			}
		} else {
			//otherwise is a byte[]
			pp.message = (byte[]) doc.get("message");
		}

		//fill alpha attributes
		Attributes alpha = new Attributes();
		DBObject alphaList = (DBObject) doc.get("alpha");
		for (String key : alphaList.keySet()) {
			//String key = dbObj.keySet().iterator().next();
			alpha.add(key, inflateType(alphaList.get(key)));
		}
		pp.alpha = alpha;

		//fill beta attributes
		Attributes beta = new Attributes();
		DBObject betaList = (DBObject) doc.get("beta");
		for (String key : betaList.keySet()) {
			//String key = dbObj.keySet().iterator().next();
			beta.add(key, inflateType(betaList.get(key)));
		}
		pp.beta = beta;

		return pp;
	}

	/**
	 * Helper method checking the type of the given object and creating the corresponding Type
	 *
	 * @param o object to check
	 * @return an object of the corresponding Type or null if the type of o is unknown
	 */
	private static Value inflateType(Object o) {
		if (o instanceof Integer) {
			return new IntegerValue((int) o);
		} else if (o instanceof String) {
			return new StringValue((String) o);
		} else if (o instanceof Date) {
			return new DateValue((Date) o);
		} else if (o instanceof byte[]) {
			return new ByteArrayValue((byte[]) o);
		} else if (o instanceof Double) {
			return new DoubleValue((Double) o);
		} else {
			return null;
		}
	}
}
