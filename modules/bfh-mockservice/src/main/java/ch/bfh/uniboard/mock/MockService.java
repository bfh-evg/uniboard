/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.mock;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import java.util.Collections;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.xml.bind.DatatypeConverter;

/**
 * The class MockService implements a mock service for testing purposes.
 *
 * @author Stephan Fischli &lt;stephan.fischli@bfh.ch&gt;
 */
@Stateless(name = "MockService")
public class MockService implements GetService, PostService {

	protected static final Logger logger = Logger.getLogger(MockService.class.getName());
	private byte[] message;
	private Attributes alpha;

	@PostConstruct
	public void init() {
		message = DatatypeConverter.parseBase64Binary("HelloSpringfield");
		alpha = new Attributes();
		alpha.add("name", new StringValue("Homer Simpson"));
		alpha.add("age", new IntegerValue(35));
	}

	@Override
	public ResultContainer get(Query query) {
		logger.info("Retrieve posts using " + query);
		Post post = new Post(message, alpha, alpha);
		return new ResultContainer(Collections.singletonList(post), alpha);
	}

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		logger.info("Post message=" + DatatypeConverter.printBase64Binary(message) + ", alpha=" + alpha + ", beta=" + beta);
		this.message = message;
		this.alpha = alpha;
		return alpha;
	}
}
