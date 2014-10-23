/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project Univote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.data;

import ch.bfh.uniboard.service.AlphaIdentifier;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.BetaIdentifier;
import ch.bfh.uniboard.service.Between;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEqual;
import ch.bfh.uniboard.service.Identifier;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEqual;
import ch.bfh.uniboard.service.MessageIdentifier;
import ch.bfh.uniboard.service.NotEqual;
import ch.bfh.uniboard.service.Order;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Transformer {

	protected static final Logger logger = Logger.getLogger(Transformer.class.getName());

	static public AttributesDTO convertAttributesToDTO(Attributes attributes) throws TransformException {

		AttributesDTO aDTO = new AttributesDTO();
		for (Map.Entry<String, Value> e : attributes.getEntries()) {
			AttributesDTO.AttributeDTO ent = new AttributesDTO.AttributeDTO();
			ent.setKey(e.getKey());
			ent.setValue(Transformer.convertValueToDTO(e.getValue()));
			aDTO.getAttribute().add(ent);
		}
		return aDTO;
	}

	static public Attributes convertAttributesDTOtoAttributes(AttributesDTO attributesDTO) throws TransformException {

		Attributes attributes = new Attributes();
		for (AttributesDTO.AttributeDTO attr : attributesDTO.getAttribute()) {
			attributes.add(attr.getKey(), Transformer.convertValueDTOToValue(attr.getValue()));
		}
		return attributes;
	}

	static public Value convertValueDTOToValue(ValueDTO valueDTO) throws TransformException {

		if (valueDTO instanceof ByteArrayValueDTO) {
			ByteArrayValueDTO tmpValue = (ByteArrayValueDTO) valueDTO;
			return new ByteArrayValue(tmpValue.getValue());
		} else if (valueDTO instanceof DateValueDTO) {
			DateValueDTO tmpValue = (DateValueDTO) valueDTO;
			return new DateValue(tmpValue.getValue().toGregorianCalendar().getTime());
		} else if (valueDTO instanceof IntegerValueDTO) {
			IntegerValueDTO tmpValue = (IntegerValueDTO) valueDTO;
			return new IntegerValue(tmpValue.getValue());
		} else if (valueDTO instanceof StringValueDTO) {
			StringValueDTO tmpValue = (StringValueDTO) valueDTO;
			return new StringValue(tmpValue.getValue());
		}
		logger.log(Level.SEVERE, "Unsupported ValueDTO type: {0}", valueDTO.getClass().getCanonicalName());
		throw new TransformException("Unsupported ValueDTO type");
	}

	static public ValueDTO convertValueToDTO(Value value) throws TransformException {
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
						ex.getMessage());
				throw new TransformException("Could not convert date to gregorian calendar");
			}
		} else if (value instanceof IntegerValue) {
			IntegerValue tmpValue = (IntegerValue) value;
			return new IntegerValueDTO(tmpValue.getValue());
		} else if (value instanceof StringValue) {
			StringValue tmpValue = (StringValue) value;
			return new StringValueDTO(tmpValue.getValue());
		}
		logger.log(Level.SEVERE, "Unsupported Value type: {0}", value.getClass().getCanonicalName());
		throw new TransformException("Unsupported Value type");
	}

	static public Identifier convertIdentifierDTOtoIdentifier(IdentifierDTO identifierDTO) throws TransformException {

		if (identifierDTO instanceof AlphaIdentifierDTO) {
			return new AlphaIdentifier(identifierDTO.getPart());
		} else if (identifierDTO instanceof BetaIdentifierDTO) {
			return new BetaIdentifier(identifierDTO.getPart());
		} else if (identifierDTO instanceof MessageIdentifierDTO) {
			return new MessageIdentifier(identifierDTO.getPart());
		} else {
			logger.log(Level.SEVERE, "Unsupported Identifier: {0}", identifierDTO.getClass().getCanonicalName());
			throw new TransformException("Unsupported Identitifer");
		}
	}

	static public Query convertQueryDTOtoQuery(QueryDTO queryDTO) throws TransformException {

		List<Constraint> constraints = new ArrayList<>();

		for (ConstraintDTO cDTO : queryDTO.getConstraint()) {
			Identifier identifier = Transformer.convertIdentifierDTOtoIdentifier(cDTO.getIdentifier());
			if (cDTO instanceof BetweenDTO) {
				BetweenDTO cTmp = (BetweenDTO) cDTO;
				Value lowB = Transformer.convertValueDTOToValue(cTmp.getLowerBound());
				Value upB = Transformer.convertValueDTOToValue(cTmp.getUpperBound());
				Between cNew = new Between(identifier, lowB, upB);
				constraints.add(cNew);
			} else if (cDTO instanceof EqualDTO) {
				EqualDTO cTmp = (EqualDTO) cDTO;
				Equal cNew = new Equal(identifier, Transformer.convertValueDTOToValue(cTmp.getValue()));
				constraints.add(cNew);
			} else if (cDTO instanceof GreaterDTO) {
				GreaterDTO cTmp = (GreaterDTO) cDTO;
				Greater cNew = new Greater(identifier, Transformer.convertValueDTOToValue(cTmp.getValue()));
				constraints.add(cNew);
			} else if (cDTO instanceof GreaterEqualDTO) {
				GreaterEqualDTO cTmp = (GreaterEqualDTO) cDTO;
				GreaterEqual cNew = new GreaterEqual(identifier,
						Transformer.convertValueDTOToValue(cTmp.getValue()));
				constraints.add(cNew);
			} else if (cDTO instanceof InDTO) {
				InDTO cTmp = (InDTO) cDTO;
				List<Value> list = new ArrayList<>();
				for (ValueDTO valueDTO : cTmp.getElement()) {
					list.add(Transformer.convertValueDTOToValue(valueDTO));
				}
				In cNew = new In(identifier, list);
				constraints.add(cNew);
			} else if (cDTO instanceof LessDTO) {
				LessDTO cTmp = (LessDTO) cDTO;
				Less cNew = new Less(identifier, Transformer.convertValueDTOToValue(cTmp.getValue()));
				constraints.add(cNew);
			} else if (cDTO instanceof LessEqualDTO) {
				LessEqualDTO cTmp = (LessEqualDTO) cDTO;
				LessEqual cNew = new LessEqual(identifier, Transformer.convertValueDTOToValue(cTmp.getValue()));
				constraints.add(cNew);
			} else if (cDTO instanceof NotEqualDTO) {
				NotEqualDTO cTmp = (NotEqualDTO) cDTO;
				NotEqual cNew = new NotEqual(identifier, Transformer.convertValueDTOToValue(cTmp.getValue()));
				constraints.add(cNew);
			}
		}
		List<Order> orders = new ArrayList<>();

		for (OrderDTO order : queryDTO.getOrder()) {

			Order tmp = new Order(Transformer.convertIdentifierDTOtoIdentifier(order.getIdentifier()), order.ascDesc);
			orders.add(tmp);
		}

		return new Query(constraints, orders, queryDTO.getLimit());
	}

	static public ResultDTO convertResulttoResultDTO(List<Post> result) throws TransformException {
		ResultDTO result2 = new ResultDTO();
		for (ch.bfh.uniboard.service.Post p : result) {
			PostDTO pNew = new PostDTO();
			pNew.setAlpha(Transformer.convertAttributesToDTO(p.getAlpha()));
			pNew.setBeta(Transformer.convertAttributesToDTO(p.getBeta()));
			pNew.setMessage(p.getMessage());
			result2.getPost().add(pNew);
		}
		return result2;
	}

	public static ResultContainerDTO convertResultContainertoResultContainerDTO(ResultContainer resultContainer)
			throws TransformException {

		ResultContainerDTO result = new ResultContainerDTO(
				Transformer.convertResulttoResultDTO(resultContainer.getResult()),
				Transformer.convertAttributesToDTO(resultContainer.getGamma()));
		return result;
	}
}
