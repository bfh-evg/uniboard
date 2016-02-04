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

import ch.bfh.uniboard.service.data.Query;
import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Observer implements Serializable {

	private String url;
	private Query query;

	public Observer() {
	}

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
