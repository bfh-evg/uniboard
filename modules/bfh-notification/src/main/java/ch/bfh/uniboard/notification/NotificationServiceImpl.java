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

import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.TransformException;
import ch.bfh.uniboard.data.Transformer;
import ch.bfh.uniboard.service.data.Query;
import static ch.bfh.unicrypt.helper.math.Alphabet.UPPER_CASE;
import ch.bfh.unicrypt.math.algebra.general.classes.FixedStringSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@WebService(serviceName = "NotificationService",
		portName = "NotificationServicePort",
		endpointInterface = "ch.bfh.uniboard.notification.NotificationService",
		targetNamespace = "http://uniboard.bfh.ch/notification/",
		wsdlLocation = "META-INF/wsdl/NotificationService.wsdl")

@Stateless
public class NotificationServiceImpl implements NotificationService {

	protected static final Logger logger = Logger.getLogger(NotificationServiceImpl.class.getName());

	@EJB
	ObserverManager observerManager;

	@Override
	public String register(String url, QueryDTO query) {

		try {
			FixedStringSet fixedStringSet = FixedStringSet.getInstance(UPPER_CASE, 20);
			String notificationCode = fixedStringSet.getRandomElement().getValue();
			Query q = Transformer.convertQueryDTOtoQuery(query);
			this.observerManager.put(notificationCode, new Observer(url, q));
			logger.log(Level.INFO, "Added url: {0} and query {1}", new Object[]{url, q.toString()});
			return notificationCode;
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			return "Error";
		}
	}

	@Override
	public void unregister(String notificationCode) {
		Observer observer = this.observerManager.remove(notificationCode);
		if (observer != null) {
			logger.log(Level.INFO, "Removed Observer:{0} Notification: {1}",
					new Object[]{observer.getUrl(), notificationCode});
		}
	}

}
