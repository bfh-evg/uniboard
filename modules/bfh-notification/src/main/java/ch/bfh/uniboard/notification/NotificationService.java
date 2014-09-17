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

import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.ByteArrayValueDTO;
import ch.bfh.uniboard.data.DateValueDTO;
import ch.bfh.uniboard.data.DoubleValueDTO;
import ch.bfh.uniboard.data.IntegerValueDTO;
import ch.bfh.uniboard.data.PostDTO;
import ch.bfh.uniboard.data.StringValueDTO;
import ch.bfh.uniboard.data.ValueDTO;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.BetaIdentifier;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.ConfigurationManager;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.DoubleValue;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.PostComponent;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class NotificationService extends PostComponent implements PostService {

	private static final String CONFIG_NAME = "bfh-notification";
	private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
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

		PostDTO post = new PostDTO(message, this.convertAttributesToDTO(alpha), this.convertAttributesToDTO(beta));
		String uniqueAttribute = this.configurationManager.getConfiguration(CONFIG_NAME).getProperty(UNIQUE_ATTRIBUTE);

		for (Entry<String, Observer> entry : this.observerManager.getObservers().entrySet()) {
			Query query = entry.getValue().getQuery();
			Constraint c = new Equal(new BetaIdentifier(uniqueAttribute), beta.getValue(uniqueAttribute));
			query.getConstraints().add(c);
			ResultContainer result = getService.get(query);
			if (!result.getResult().isEmpty()) {
				this.observerClient.notifyObserver(entry.getValue().getUrl(), post);
			}
		}
	}

	protected AttributesDTO convertAttributesToDTO(Attributes attributes) {

		AttributesDTO aDTO = new AttributesDTO();
		for (Map.Entry<String, Value> e : attributes.getEntries()) {
			AttributesDTO.AttributeDTO ent = new AttributesDTO.AttributeDTO();
			ent.setKey(e.getKey());
			ent.setValue(this.convertValueToDTO(e.getValue()));
			aDTO.getAttribute().add(ent);
		}
		return aDTO;
	}

	protected ValueDTO convertValueToDTO(Value value) {
		if (value instanceof ByteArrayValue) {
			ByteArrayValue tmpValue = (ByteArrayValue) value;
			return new ByteArrayValueDTO(tmpValue.getValue());
		} else if (value instanceof DateValue) {
			DateValue tmpValue = (DateValue) value;
			DateValueDTO tmpValueDTO = new DateValueDTO();

			try {
				GregorianCalendar c = new GregorianCalendar();
				c.setTime(tmpValue.getValue());
				XMLGregorianCalendar date = DatatypeFactory.newInstance().newXMLGregorianCalendar(c).normalize();
				tmpValueDTO.setValue(date);
				return tmpValueDTO;
			} catch (DatatypeConfigurationException ex) {
				logger.log(Level.WARNING, "{0}Could not convert date to gregorian calendar: ",
						tmpValue.getValue().toString());
			}
		} else if (value instanceof DoubleValue) {
			DoubleValue tmpValue = (DoubleValue) value;
			return new DoubleValueDTO(tmpValue.getValue());
		} else if (value instanceof IntegerValue) {
			IntegerValue tmpValue = (IntegerValue) value;
			return new IntegerValueDTO(tmpValue.getValue());
		} else if (value instanceof StringValue) {
			StringValue tmpValue = (StringValue) value;
			return new StringValueDTO(tmpValue.getValue());
		}
		logger.log(Level.SEVERE, "Unsupported Value type: {0}", value.getClass().getCanonicalName());
		return new StringValueDTO("");
	}
}
