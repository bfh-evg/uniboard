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
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class ObserverClientImpl implements ObserverClient {

	private static final Logger logger = Logger.getLogger(ObserverClientImpl.class.getName());

	@Override
	public void notifyObserver(String endpointUrl, PostDTO post) {
		ObserverService observer;
		try {
			URL wsdlLocation = new URL(endpointUrl);
			QName qname = new QName("http://uniboard.bfh.ch/notification/", "ObserverService");
			ObserverService_Service observerService = new ObserverService_Service(wsdlLocation, qname);
			observer = observerService.getObserverServicePort();
			BindingProvider bp = (BindingProvider) observer;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
			observer.notify(post);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Unable to notify Observer: {0}, exception: {1}",
					new Object[]{endpointUrl, ex});
		}
	}

}
