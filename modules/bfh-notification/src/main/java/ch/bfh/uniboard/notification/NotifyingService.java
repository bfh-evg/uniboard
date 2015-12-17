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

import ch.bfh.uniboard.data.PostDTO;
import ch.bfh.uniboard.data.TransformException;
import ch.bfh.uniboard.data.Transformer;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.BetaIdentifier;
import ch.bfh.uniboard.service.Configuration;
import ch.bfh.uniboard.service.ConfigurationManager;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.PostComponent;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class NotifyingService extends PostComponent implements PostService {

	private static final String CONFIG_NAME = "bfh-notification";
	private static final Logger logger = Logger.getLogger(NotifyingService.class.getName());
	private static final String UNIQUE_ATTRIBUTE = "unique";

	@EJB
	PostService postSuccessor;

	@EJB
	GetService getService;

	@EJB
	ObserverManager observerManager;

	@EJB
	ConfigurationManager configurationManager;

	@EJB
	ObserverClient observerClient;

	@Override
	protected PostService getPostSuccessor() {
		return this.postSuccessor;
	}

	@Override
	protected void afterPost(byte[] message, Attributes alpha, Attributes beta) {

		Configuration config = this.configurationManager.getConfiguration(CONFIG_NAME);
		if (config == null) {
			logger.log(Level.SEVERE,
					"Configuration for the notification service is not available. Keyword: " + CONFIG_NAME);
			return;
		}
		String uniqueAttribute = config.getEntries().get(UNIQUE_ATTRIBUTE);
		if (uniqueAttribute == null) {
			logger.log(Level.SEVERE,
					"Configuration for the notification service is not complete. Attribute: " + UNIQUE_ATTRIBUTE);
			return;
		}
		try {
			PostDTO post = new PostDTO(message, Transformer.convertAttributesToDTO(alpha),
					Transformer.convertAttributesToDTO(beta));
			logger.log(Level.FINE, this.observerManager.getObservers().toString());
			for (Entry<String, Observer> entry : this.observerManager.getObservers().entrySet()) {
				Query query = entry.getValue().getQuery();
				Constraint c = new Equal(new BetaIdentifier(uniqueAttribute), beta.getValue(uniqueAttribute));
				query.getConstraints().add(c);
				logger.log(Level.FINE, query.toString());
				ResultContainer result = getService.get(query);
				query.getConstraints().remove(c);
				if (!result.getResult().isEmpty()) {
					logger.log(Level.INFO, "Notifing observer: {0}", entry.getValue().getUrl());
					this.observerClient.notifyObserver(entry.getValue().getUrl(), entry.getKey(), post);
				}
			}
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, "Could not notify observers: ", ex);
		}
	}
}
