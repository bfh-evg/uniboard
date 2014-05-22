/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniVote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.z.ejb.component1;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.Service;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless
@Local(value = Service.class)
public class TerminatingComponentBean implements Service {

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		System.out.println("post() called.");
		return beta;
	}

	@Override
	public ResultContainer get(Query query) {
		System.out.println("get() called.");
		ResultContainer r = new ResultContainer(null, null);
		return r;
	}
}
