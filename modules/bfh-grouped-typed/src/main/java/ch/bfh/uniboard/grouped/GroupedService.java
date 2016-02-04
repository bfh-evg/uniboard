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
package ch.bfh.uniboard.grouped;

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.uniboard.service.PostComponent;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.StringValue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class GroupedService extends PostComponent implements PostService {

	private static final String ATTRIBUTE_NAME = "group";
	private static final String CONFIG_NAME = "bfh-grouped-typed";

	private static final Logger logger = Logger.getLogger(GroupedService.class.getName());

	@EJB(beanName = "TypedService")
	PostService postSuccessor;

	@EJB
	ConfigurationManager configurationManager;

	@Override
	protected PostService getPostSuccessor() {
		return this.postSuccessor;
	}

	@Override
	protected Attributes beforePost(byte[] message, Attributes alpha, Attributes beta) {

		if (!alpha.containsKey(ATTRIBUTE_NAME)) {
			beta.add(Attributes.REJECTED, new StringValue("BGT-001 Missing required attribute: " + ATTRIBUTE_NAME));
			return beta;
		}
		if (!(alpha.getValue(ATTRIBUTE_NAME) instanceof StringValue)) {
			beta.add(Attributes.REJECTED, new StringValue("BGT-002 Required attribute: " + ATTRIBUTE_NAME
					+ " is not of type string."));
			return beta;
		}
		StringValue group = (StringValue) alpha.getValue(ATTRIBUTE_NAME);
		Configuration p = this.configurationManager.getConfiguration(CONFIG_NAME);
		if (p == null) {
			logger.log(Level.SEVERE, "Configuration for component " + CONFIG_NAME + " is missing.");
			beta.add(Attributes.ERROR,
					new StringValue("BGT-003 This UniBoard instance is down due to a configuration error."));
			return beta;
		}
		if (!p.getEntries().containsKey(group.getValue())) {
			beta.add(Attributes.REJECTED,
					new StringValue("BGT-004 Unknown " + ATTRIBUTE_NAME + ": " + group.getValue()));
			return beta;
		}
		return beta;
	}

}
