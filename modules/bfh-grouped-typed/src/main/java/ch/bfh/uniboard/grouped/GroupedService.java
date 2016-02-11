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
import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.DataType;
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
			beta.add(new Attribute(Attributes.REJECTED, "BGT-001 Missing required attribute: " + ATTRIBUTE_NAME));
			return beta;
		}
		if (alpha.getAttribute(ATTRIBUTE_NAME).getDataType() != null
				&& alpha.getAttribute(ATTRIBUTE_NAME).getDataType() != DataType.STRING) {
			beta.add(new Attribute(Attributes.REJECTED, "BGT-002 Required attribute: " + ATTRIBUTE_NAME
					+ " is not of type string."));
			return beta;
		}
		String group = alpha.getAttribute(ATTRIBUTE_NAME).getValue();
		Configuration p = this.configurationManager.getConfiguration(CONFIG_NAME);
		if (p == null) {
			logger.log(Level.SEVERE, "Configuration for component " + CONFIG_NAME + " is missing.");
			beta.add(new Attribute(Attributes.ERROR,
					"BGT-003 This UniBoard instance is down due to a configuration error."));
			return beta;
		}
		if (!p.getEntries().containsKey(group)) {
			beta.add(new Attribute(Attributes.REJECTED,
					"BGT-004 Unknown " + ATTRIBUTE_NAME + ": " + group));
			return beta;
		}
		return beta;
	}

}
