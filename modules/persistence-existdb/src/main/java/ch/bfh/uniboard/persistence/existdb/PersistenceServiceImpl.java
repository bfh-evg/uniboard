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
package ch.bfh.uniboard.persistence.existdb;

import ch.bfh.uniboard.core.Query;
import ch.bfh.uniboard.core.Request;
import ch.bfh.uniboard.core.Response;
import ch.bfh.uniboard.core.Result;
import ch.bfh.uniboard.persistence.service.PersistenceService;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class PersistenceServiceImpl implements PersistenceService {

	@Override
	public Response post(Request request) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Result get(Query query) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
