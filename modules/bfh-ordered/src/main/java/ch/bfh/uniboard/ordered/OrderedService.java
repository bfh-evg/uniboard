
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
package ch.bfh.uniboard.ordered;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ConfigurationManager;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;

/**
 * Provides the ordered property per section.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
public class OrderedService implements PostService {

	private static final String ATTRIBUTE_NAME = "rank";
	private static final String STATE_NAME = "bfh-ordered";
	private static final String SECTIONED_NAME = "section";
	protected OrderedState state;

	@EJB
	PostService postSuccessor;

	@EJB
	ConfigurationManager configurationManager;

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		Value sectionValue = alpha.getValue(SECTIONED_NAME);
		String section = ((StringValue) sectionValue).getValue();
		Integer order = this.state.getSections().getOrDefault(section, 0);
		order++;
		beta.add(ATTRIBUTE_NAME, new IntegerValue(order));
		Attributes newBeta = this.postSuccessor.post(message, alpha, beta);
		//If no error happend further below safe the new head
		if (!(newBeta.getKeys().contains(Attributes.ERROR) || newBeta.getKeys().contains(Attributes.REJECTED))) {
			this.state.getSections().put(section, order);
		}
		return newBeta;
	}

	@PostConstruct
	protected void init() {
		this.state = configurationManager.loadState(STATE_NAME, OrderedState.class);
		if (this.state == null) {
			this.state = new OrderedState();
			this.state.setKey(STATE_NAME);
		}
	}

	@PreDestroy
	protected void save() {
		configurationManager.saveState(state);
	}
}
