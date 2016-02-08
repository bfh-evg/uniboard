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
package ch.bfh.uniboard.sectioned;

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
 * Component that validates the section of incoming post requests. The list of valid sections is loaded over the
 * configuration manager.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class SectionedService extends PostComponent implements PostService {

	private static final String ATTRIBUTE_NAME = "section";
	private static final String CONFIG_NAME = "bfh-sectioned";

	private static final Logger logger = Logger.getLogger(SectionedService.class.getName());

	@EJB
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
			beta.add(Attributes.REJECTED, new StringValue("BSE-001 Missing required attribute: " + ATTRIBUTE_NAME));
			return beta;
		}
		if (!(alpha.getAttribute(ATTRIBUTE_NAME) instanceof StringValue)) {
			beta.add(Attributes.REJECTED, new StringValue("BSE-002 Required attribute: " + ATTRIBUTE_NAME
					+ " is not of type string."));
			return beta;
		}
		StringValue section = (StringValue) alpha.getAttribute(ATTRIBUTE_NAME);
		Configuration p = this.configurationManager.getConfiguration(CONFIG_NAME);
		if (p == null) {
			logger.log(Level.SEVERE, "Configuration for component " + CONFIG_NAME + " is missing.");
			beta.add(Attributes.ERROR,
					new StringValue("BSE-003 This UniBoard instance is down due to a configuration error."));
			return beta;
		}
		if (!p.getEntries().containsValue(section.getValue())) {
			beta.add(Attributes.REJECTED,
					new StringValue("BSE-004 Unknown " + ATTRIBUTE_NAME + ": " + section.getValue()));
			return beta;
		}
		return beta;
	}

}
