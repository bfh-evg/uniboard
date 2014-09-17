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
package ch.bfh.uniboard.notification;

import ch.bfh.uniboard.service.Query;
import java.io.Serializable;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Observer implements Serializable {

	private final String url;
	private final Query query;

	public Observer(String url, Query query) {
		this.url = url;
		this.query = query;
	}

	public String getUrl() {
		return url;
	}

	public Query getQuery() {
		return query;
	}

}
