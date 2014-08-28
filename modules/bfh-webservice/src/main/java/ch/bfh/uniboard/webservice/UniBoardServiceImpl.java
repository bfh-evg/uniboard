/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.webservice;

import ch.bfh.uniboard.UniBoardService;
import ch.bfh.uniboard.data.*;
import ch.bfh.uniboard.data.AttributesDTO.AttributeDTO;
import ch.bfh.uniboard.service.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@WebService(serviceName = "UniBoardService",
		portName = "UniBoardServicePort",
		endpointInterface = "ch.bfh.uniboard.UniBoardService",
		targetNamespace = "http://uniboard.bfh.ch/",
		wsdlLocation = "META-INF/wsdl/UniBoardService.wsdl")

@Stateless
public class UniBoardServiceImpl implements UniBoardService {

	protected static final Logger logger
			= Logger.getLogger(UniBoardServiceImpl.class.getName());

	@EJB
	private PostService postSuccessor;
	@EJB
	private GetService getSuccessor;

	@Override
	public ResultContainerDTO get(QueryDTO query) {

		List<Constraint> constraints = new ArrayList<>();

		try {
			for (ConstraintDTO cDTO : query.getConstraint()) {
				Identifier identifier = this.convertIdentifierDTOtoIdentifier(cDTO.getIdentifier());
				if (cDTO instanceof BetweenDTO) {
					BetweenDTO cTmp = (BetweenDTO) cDTO;
					Value lowB = this.convertValueDTOToValue(cTmp.getLowerBound());
					Value upB = this.convertValueDTOToValue(cTmp.getUpperBound());
					Between cNew = new Between(identifier, lowB, upB);
					constraints.add(cNew);
				} else if (cDTO instanceof EqualDTO) {
					EqualDTO cTmp = (EqualDTO) cDTO;
					Equal cNew = new Equal(identifier, this.convertValueDTOToValue(cTmp.getValue()));
					constraints.add(cNew);
				} else if (cDTO instanceof GreaterDTO) {
					GreaterDTO cTmp = (GreaterDTO) cDTO;
					Greater cNew = new Greater(identifier, this.convertValueDTOToValue(cTmp.getValue()));
					constraints.add(cNew);
				} else if (cDTO instanceof GreaterEqualDTO) {
					GreaterEqualDTO cTmp = (GreaterEqualDTO) cDTO;
					GreaterEqual cNew = new GreaterEqual(identifier, this.convertValueDTOToValue(cTmp.getValue()));
					constraints.add(cNew);
				} else if (cDTO instanceof InDTO) {
					InDTO cTmp = (InDTO) cDTO;
					List<Value> list = new ArrayList<>();
					for (ValueDTO valueDTO : cTmp.getElement()) {
						list.add(this.convertValueDTOToValue(valueDTO));
					}
					In cNew = new In(identifier, list);
					constraints.add(cNew);
				} else if (cDTO instanceof LessDTO) {
					LessDTO cTmp = (LessDTO) cDTO;
					Less cNew = new Less(identifier, this.convertValueDTOToValue(cTmp.getValue()));
					constraints.add(cNew);
				} else if (cDTO instanceof LessEqualDTO) {
					LessEqualDTO cTmp = (LessEqualDTO) cDTO;
					LessEqual cNew = new LessEqual(identifier, this.convertValueDTOToValue(cTmp.getValue()));
					constraints.add(cNew);
				} else if (cDTO instanceof NotEqualDTO) {
					NotEqualDTO cTmp = (NotEqualDTO) cDTO;
					NotEqual cNew = new NotEqual(identifier, this.convertValueDTOToValue(cTmp.getValue()));
					constraints.add(cNew);
				}
			}
		} catch (UniBoardServiceException ex) {
			AttributesDTO exAttributes = new AttributesDTO();
			AttributeDTO e = new AttributeDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(new StringValueDTO(ex.getMessage()));
			exAttributes.getAttribute().add(e);
			ResultDTO exResult = new ResultDTO();
			ResultContainerDTO exResultContainer = new ResultContainerDTO(exResult, exAttributes);
			return exResultContainer;
		}
		Query q = new Query(constraints);

		ResultContainer rContainer = this.getSuccessor.get(q);

		try {
			ResultDTO result = new ResultDTO();
			for (ch.bfh.uniboard.service.Post p : rContainer.getResult()) {
				PostDTO pNew = new PostDTO();
				pNew.setAlpha(this.convertAttributesToDTO(p.getAlpha()));
				pNew.setBeta(this.convertAttributesToDTO(p.getBeta()));
				pNew.setMessage(p.getMessage());
				result.getPost().add(pNew);
			}

			ResultContainerDTO resultContainer = new ResultContainerDTO();
			resultContainer.setResult(result);
			resultContainer.setGamma(this.convertAttributesToDTO(rContainer.getGamma()));
			return resultContainer;
		} catch (UniBoardServiceException ex) {
			AttributesDTO exAttributes = new AttributesDTO();
			AttributeDTO e = new AttributeDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(new StringValueDTO(ex.getMessage()));
			exAttributes.getAttribute().add(e);
			ResultDTO exResult = new ResultDTO();
			ResultContainerDTO exResultContainer = new ResultContainerDTO(exResult, exAttributes);
			return exResultContainer;
		}
	}

	@Override
	public AttributesDTO post(byte[] message, AttributesDTO alpha) {

		try {
			Attributes alphaIntern = new Attributes();
			for (AttributeDTO e : alpha.getAttribute()) {
				alphaIntern.add(e.getKey(), this.convertValueDTOToValue(e.getValue()));
			}
			Attributes betaIntern = new Attributes();

			betaIntern = this.postSuccessor.post(message, alphaIntern, betaIntern);
			AttributesDTO response = this.convertAttributesToDTO(betaIntern);
			return response;

		} catch (UniBoardServiceException ex) {
			AttributesDTO exAttributes = new AttributesDTO();
			AttributeDTO e = new AttributeDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(new StringValueDTO(ex.getMessage()));
			exAttributes.getAttribute().add(e);
			return exAttributes;
		}
	}

	protected AttributesDTO convertAttributesToDTO(Attributes attributes) throws UniBoardServiceException {

		AttributesDTO aDTO = new AttributesDTO();
		for (Map.Entry<String, Value> e : attributes.getEntries()) {
			AttributeDTO ent = new AttributeDTO();
			ent.setKey(e.getKey());
			ent.setValue(this.convertValueToDTO(e.getValue()));
			aDTO.getAttribute().add(ent);
		}
		return aDTO;
	}

	protected Value convertValueDTOToValue(ValueDTO valueDTO) throws UniBoardServiceException {

		if (valueDTO instanceof ByteArrayValueDTO) {
			ByteArrayValueDTO tmpValue = (ByteArrayValueDTO) valueDTO;
			return new ByteArrayValue(tmpValue.getValue());
		} else if (valueDTO instanceof DateValueDTO) {
			DateValueDTO tmpValue = (DateValueDTO) valueDTO;
			return new DateValue(tmpValue.getValue().toGregorianCalendar().getTime());
		} else if (valueDTO instanceof DoubleValueDTO) {
			DoubleValueDTO tmpValue = (DoubleValueDTO) valueDTO;
			return new DoubleValue(tmpValue.getValue());
		} else if (valueDTO instanceof IntegerValueDTO) {
			IntegerValueDTO tmpValue = (IntegerValueDTO) valueDTO;
			return new IntegerValue(tmpValue.getValue());
		} else if (valueDTO instanceof StringValueDTO) {
			StringValueDTO tmpValue = (StringValueDTO) valueDTO;
			return new StringValue(tmpValue.getValue());
		}
		logger.log(Level.SEVERE, "Unsupported ValueDTO type: {0}", valueDTO.getClass().getCanonicalName());
		throw new UniBoardServiceException("Unsupported ValueDTO type");
	}

	protected ValueDTO convertValueToDTO(Value value) throws UniBoardServiceException {
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
				throw new UniBoardServiceException("Could not convert date to gregorian calendar");
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
		throw new UniBoardServiceException("Unsupported Value type");
	}

	private Identifier convertIdentifierDTOtoIdentifier(IdentifierDTO identifierDTO) throws UniBoardServiceException {

		if (identifierDTO instanceof AlphaIdentifierDTO) {
			return new AlphaIdentifier(identifierDTO.getPart());
		} else if (identifierDTO instanceof BetaIdentifierDTO) {
			return new BetaIdentifier(identifierDTO.getPart());
		} else if (identifierDTO instanceof MessageIdentifierDTO) {
			return new MessageIdentifier(identifierDTO.getPart());
		} else {
			logger.log(Level.SEVERE, "Unsupported Identifier: {0}", identifierDTO.getClass().getCanonicalName());
			throw new UniBoardServiceException("Unsupported Identitifer");
		}
	}

}
