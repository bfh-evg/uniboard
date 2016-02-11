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

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.ResultContainer;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.UniBoardService;
import ch.bfh.uniboard.data.*;
import ch.bfh.uniboard.data.AttributesDTO.AttributeDTO;
import ch.bfh.uniboard.service.*;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

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

		try {
			Query q = Transformer.convertQueryDTOtoQuery(query);

			ResultContainer rContainer = this.getSuccessor.get(q);

			return Transformer.convertResultContainertoResultContainerDTO(rContainer);
		} catch (TransformException ex) {
			AttributesDTO exAttributes = new AttributesDTO();
			AttributeDTO e = new AttributeDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(ex.getMessage());
			exAttributes.getAttribute().add(e);
			ResultDTO exResult = new ResultDTO();
			ResultContainerDTO exResultContainer = new ResultContainerDTO(exResult, exAttributes);
			return exResultContainer;
		}
	}

	@Override
	public AttributesDTO post(byte[] message, AttributesDTO alpha
	) {

		try {
			Attributes alphaIntern = Transformer.convertAttributesDTOtoAttributes(alpha);
			Attributes betaIntern = new Attributes();

			betaIntern = this.postSuccessor.post(message, alphaIntern, betaIntern);
			AttributesDTO response = Transformer.convertAttributesToDTO(betaIntern);
			return response;

		} catch (TransformException ex) {
			AttributesDTO exAttributes = new AttributesDTO();
			AttributeDTO e = new AttributeDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(ex.getMessage());
			exAttributes.getAttribute().add(e);
			return exAttributes;
		}
	}

}
