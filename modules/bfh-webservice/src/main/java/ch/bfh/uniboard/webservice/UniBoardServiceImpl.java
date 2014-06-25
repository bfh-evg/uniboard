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

import ch.bfh.uniboard.service.*;
import ch.bfh.uniboard.webservice.data.AttributesDTO;
import ch.bfh.uniboard.webservice.data.AttributesDTO.EntryDTO;
import ch.bfh.uniboard.webservice.data.BetweenDTO;
import ch.bfh.uniboard.webservice.data.ByteArrayValueDTO;
import ch.bfh.uniboard.webservice.data.ConstraintDTO;
import ch.bfh.uniboard.webservice.data.DateValueDTO;
import ch.bfh.uniboard.webservice.data.DoubleValueDTO;
import ch.bfh.uniboard.webservice.data.EqualsDTO;
import ch.bfh.uniboard.webservice.data.GreaterDTO;
import ch.bfh.uniboard.webservice.data.GreaterEqualsDTO;
import ch.bfh.uniboard.webservice.data.InDTO;
import ch.bfh.uniboard.webservice.data.IntegerValueDTO;
import ch.bfh.uniboard.webservice.data.LessDTO;
import ch.bfh.uniboard.webservice.data.LessEqualsDTO;
import ch.bfh.uniboard.webservice.data.PostDTO;
import ch.bfh.uniboard.webservice.data.QueryDTO;
import ch.bfh.uniboard.webservice.data.ResultContainerDTO;
import ch.bfh.uniboard.webservice.data.ResultDTO;
import ch.bfh.uniboard.webservice.data.StringValueDTO;
import ch.bfh.uniboard.webservice.data.ValueDTO;
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
@WebService(name = "UniBoardService",
		targetNamespace = "http://webservice.uniboard.bfh.ch/",
		wsdlLocation = "WEB-INF/wsdl/UniBoardService.wsdl")
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
			for (ConstraintDTO cDTO : query.getBetweenOrInOrLess()) {
				if (cDTO instanceof BetweenDTO) {
					BetweenDTO cTmp = (BetweenDTO) cDTO;
					Value start = this.convertValueDTOToValue(cTmp.getStart());
					Value end = this.convertValueDTOToValue(cTmp.getEnd());
					Between cNew = new Between(start, end, cTmp.getKey(),
							PostElement.valueOf(cTmp.getPostElement().value()));
					constraints.add(cNew);
				} else if (cDTO instanceof EqualsDTO) {
					EqualsDTO cTmp = (EqualsDTO) cDTO;
					Equals cNew = new Equals(this.convertValueDTOToValue(cTmp.getValue()), cTmp.getKey(),
							PostElement.valueOf(cTmp.getPostElement().value()));
					constraints.add(cNew);
				} else if (cDTO instanceof GreaterDTO) {
					GreaterDTO cTmp = (GreaterDTO) cDTO;
					Greater cNew = new Greater(this.convertValueDTOToValue(cTmp.getValue()), cTmp.getKey(),
							PostElement.valueOf(cTmp.getPostElement().value()));
					constraints.add(cNew);
				} else if (cDTO instanceof GreaterEqualsDTO) {
					GreaterEqualsDTO cTmp = (GreaterEqualsDTO) cDTO;
					GreaterEquals cNew = new GreaterEquals(this.convertValueDTOToValue(cTmp.getValue()), cTmp.getKey(),
							PostElement.valueOf(cTmp.getPostElement().value()));
					constraints.add(cNew);
				} else if (cDTO instanceof InDTO) {
					InDTO cTmp = (InDTO) cDTO;
					List<Value> list = new ArrayList<>();
					for (ValueDTO valueDTO : cTmp.getElement()) {
						list.add(this.convertValueDTOToValue(valueDTO));
					}
					In cNew = new In(list, cTmp.getKey(),
							PostElement.valueOf(cTmp.getPostElement().value()));
					constraints.add(cNew);
				} else if (cDTO instanceof LessDTO) {
					LessDTO cTmp = (LessDTO) cDTO;
					Less cNew = new Less(this.convertValueDTOToValue(cTmp.getValue()), cTmp.getKey(),
							PostElement.valueOf(cTmp.getPostElement().value()));
					constraints.add(cNew);
				} else if (cDTO instanceof LessEqualsDTO) {
					LessEqualsDTO cTmp = (LessEqualsDTO) cDTO;
					LessEquals cNew = new LessEquals(this.convertValueDTOToValue(cTmp.getValue()), cTmp.getKey(),
							PostElement.valueOf(cTmp.getPostElement().value()));
					constraints.add(cNew);
				}
			}
		} catch (UniBoardServiceException ex) {
			AttributesDTO exAttributes = new AttributesDTO();
			EntryDTO e = new EntryDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(new StringValueDTO(ex.getMessage()));
			exAttributes.getEntry().add(e);
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
			EntryDTO e = new EntryDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(new StringValueDTO(ex.getMessage()));
			exAttributes.getEntry().add(e);
			ResultDTO exResult = new ResultDTO();
			ResultContainerDTO exResultContainer = new ResultContainerDTO(exResult, exAttributes);
			return exResultContainer;
		}
	}

	@Override
	public AttributesDTO post(byte[] message, AttributesDTO alpha) {

		try {
			Attributes alphaIntern = new Attributes();
			for (EntryDTO e : alpha.getEntry()) {
				alphaIntern.add(e.getKey(), this.convertValueDTOToValue(e.getValue()));
			}
			Attributes betaIntern = new Attributes();

			betaIntern = this.postSuccessor.post(message, alphaIntern, betaIntern);
			AttributesDTO response = this.convertAttributesToDTO(betaIntern);
			return response;

		} catch (UniBoardServiceException ex) {
			AttributesDTO exAttributes = new AttributesDTO();
			EntryDTO e = new EntryDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(new StringValueDTO(ex.getMessage()));
			exAttributes.getEntry().add(e);
			return exAttributes;
		}
	}

	protected AttributesDTO convertAttributesToDTO(Attributes attributes) throws UniBoardServiceException {

		AttributesDTO aDTO = new AttributesDTO();
		for (Map.Entry<String, Value> e : attributes.getEntries()) {
			EntryDTO ent = new EntryDTO();
			ent.setKey(e.getKey());
			ent.setValue(this.convertValueToDTO(e.getValue()));
			aDTO.getEntry().add(ent);
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

}
