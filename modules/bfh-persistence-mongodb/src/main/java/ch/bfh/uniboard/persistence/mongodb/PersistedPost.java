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

import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.uniboard.service.data.Post;
import com.mongodb.util.JSON;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.bson.json.JsonParseException;

/**
 * A persisted post represents the posted message and all belonging attributes in the format in which it is persisted
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
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
     * @return a Document format of the PersistedPost
	 * @throws java.text.ParseException thrown when some data from the database can not be parsed.
     */
    public Document toDocument() throws ParseException {
		Document doc = new Document();

		//Save raw message
		doc.put("message", Base64.getEncoder().encodeToString(message));

		//Check if message is a JSON message
		Document jsonMessageContent = null;
		try {
			jsonMessageContent = Document.parse(new String(message, "UTF-8"));
		} catch (JsonParseException | UnsupportedEncodingException ex) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO,
					"Message is not a JSON string {0}", ex.getMessage());
		}

		if (jsonMessageContent != null) {
			//save message as JSON content
			Document jsonMessage = new Document("searchable-message", jsonMessageContent);
			doc.putAll(jsonMessage);
		}

		//Prepares the Alpha attributes
		Document alphaList = new Document();
		for (Entry<String, Attribute> entry : alpha.getEntries()) {
			if (entry.getValue().getDataType() != null && entry.getValue().getDataType() != DataType.STRING) {
				alphaList.put(entry.getKey(),
						transformFromString(entry.getValue().getValue(), entry.getValue().getDataType()));
			} else {
				alphaList.put(entry.getKey(), entry.getValue().getValue());
			}
		}
		doc.put("alpha", alphaList);

		//Prepares the Beta attributes
		Document betaList = new Document();
		for (Entry<String, Attribute> entry : beta.getEntries()) {
			if (entry.getValue().getDataType() != null && entry.getValue().getDataType() != DataType.STRING) {
				betaList.put(entry.getKey(),
						transformFromString(entry.getValue().getValue(), entry.getValue().getDataType()));
			} else {
				betaList.put(entry.getKey(), entry.getValue().getValue());
			}
		}
		doc.put("beta", betaList);

		return doc;
	}

	/**
	 * Method allowing to retrieve a PersistedPost out of the DBObject returned by the database. If the passed Document
	 * does not represent a PersistedPost, an empty PersistedPost is returned
	 *
	 * @param doc the Document returned by the database
	 * @return the corresponding persisted post
	 */
	public static PersistedPost fromDocument(Document doc) {
		PersistedPost pp = new PersistedPost();

		//TODO remove try catch when DB will be cleaned
		//this is only needed since some messages in MongoDB are not byte array
		//but string (historical reasons
		try {
			pp.message = Base64.getDecoder().decode((String) doc.get("message"));
		} catch (ClassCastException e) {
			pp.message = JSON.serialize(doc.get("message")).getBytes();
		}

		//fill alpha attributes
		Attributes alpha = new Attributes();
		Document alphaList = doc.get("alpha", Document.class);
		for (String key : alphaList.keySet()) {
			//String key = dbObj.keySet().iterator().next();
			alpha.add(transformToAttribute(key, alphaList.get(key)));
		}
		pp.alpha = alpha;

		//fill beta attributes
		Attributes beta = new Attributes();
		Document betaList = doc.get("beta", Document.class);
		for (String key : betaList.keySet()) {
			//String key = dbObj.keySet().iterator().next();
			beta.add(transformToAttribute(key, betaList.get(key)));
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
	private static Attribute transformToAttribute(String key, Object o) {
		if (o instanceof Integer) {
			Integer tmp = (Integer) o;
			return new Attribute(key, Integer.toString(tmp, 10), DataType.INTEGER);
		} else if (o instanceof Double) {
			Double tmp = (Double) o;
			return new Attribute(key, Double.toString(tmp), DataType.INTEGER);
		} else if (o instanceof String) {
			String tmp = (String) o;
			return new Attribute(key, tmp);
		} else if (o instanceof Date) {
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			dateFormat.setTimeZone(timeZone);
			return new Attribute(key, dateFormat.format(o), DataType.DATE);
		} else {
			return null;
		}
	}

	private static Object transformFromString(String value, DataType dataType) throws ParseException {
		if (dataType == DataType.INTEGER) {
			return Integer.parseInt(value);
		} else if (dataType == DataType.DATE) {
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
			dateFormat.setTimeZone(timeZone);
			return dateFormat.parse(value);
		}
		return null;
	}
}
