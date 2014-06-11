/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project Univote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.webservice;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.Service;
import ch.bfh.uniboard.webservice.data.Attributes.Entry;
import java.util.Map;
import javax.ejb.EJB;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class UniBoardServiceImpl implements UniBoardService {

	@EJB
	private Service successor;

	@Override
	public ch.bfh.uniboard.webservice.data.ResultContainer get(ch.bfh.uniboard.webservice.data.Query query) {

		Query q = new Query(null);

		this.successor.get(null);

		return null;
	}

	@Override
	public ch.bfh.uniboard.webservice.data.Attributes post(byte[] message, ch.bfh.uniboard.webservice.data.Attributes alpha) {

		Attributes alphaIntern = new Attributes();
		for (Entry e : alpha.getEntry()) {
			alphaIntern.add(e.getKey(), e.getValue());
		}

		Attributes betaIntern = new Attributes();

		betaIntern = this.successor.post(message, alphaIntern, betaIntern);

		ch.bfh.uniboard.webservice.data.Attributes beta = new ch.bfh.uniboard.webservice.data.Attributes();
		for (Map.Entry<String, Object> e : betaIntern.getEntries()) {
			Entry ent = new Entry();
			ent.setKey(e.getKey());
			ent.setValue(e.getValue());
			beta.getEntry().add(ent);
		}

		return beta;
	}

}
