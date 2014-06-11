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
package ch.bfh.uniboard.z.ejb.component2;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Component;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.Service;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless
@Local(value = Service.class)
public class Component2Bean extends Component implements Service {

	@EJB(beanName = "TerminatingComponentBean")
	private Service successor;

	@Override
	protected Service getPostSuccessor() {
		return this.successor;
	}

//    @Override
//    protected Result afterGet(Result result) throws UniBoardException {
//        System.out.println("afterGet() called.");
//        return result;
//    }
//
//    @Override
//    protected Query beforeGet(Query query) throws UniBoardException {
//        System.out.println("beforeGet() called.");
//        return super.beforeGet(query);
//    }
//
//    @Override
//    protected Response afterPost(Response response) throws UniBoardException {
//        System.out.println("afterPost() called.");
//        return super.afterPost(response);
//    }
//
//    @Override
//    protected Message beforePost(Message message) throws UniBoardException {
//        System.out.println("beforePost() called.");
//        return super.beforePost(message);
//    }
	@Override
	protected Attributes afterGet(Query query, ResultContainer resultContainer) {
		System.out.println("afterGet() called.");
		return super.afterGet(query, resultContainer);
	}

	@Override
	protected void beforeGet(Query query) {
		System.out.println("beforeGet() called.");
		super.beforeGet(query);
	}

	@Override
	protected void afterPost(byte[] message, Attributes alpha, Attributes beta) {
		System.out.println("afterPost() called.");
		super.afterPost(message, alpha, beta);
	}

	@Override
	protected Attributes beforePost(byte[] message, Attributes alpha, Attributes beta) {
		System.out.println("beforePost() called.");
		return super.beforePost(message, alpha, beta);
	}
}
