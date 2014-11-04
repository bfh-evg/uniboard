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
package ch.bfh.uniboard.restservice;

import ch.bfh.uniboard.data.AlphaIdentifierDTO;
import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.ConstraintDTO;
import ch.bfh.uniboard.data.EqualDTO;
import ch.bfh.uniboard.data.IdentifierDTO;
import ch.bfh.uniboard.data.IntegerValueDTO;
import ch.bfh.uniboard.data.OrderDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.data.TransformException;
import ch.bfh.uniboard.data.Transformer;
import ch.bfh.uniboard.data.ValueDTO;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 *
 * @author Stephan Fischli &lt;stephan.fischli@bfh.ch&gt;
 */
@Stateless
public class UniBoardRestServiceBean implements UniBoardRestService {

	protected static final Logger logger = Logger.getLogger(UniBoardRestServiceBean.class.getName());

	@EJB
	private GetService getSuccessor;

	@EJB
	private PostService postSuccessor;

	@PostConstruct
	public void init() {
		try {
			logger.info("Using JAXB provider " + JAXBContext.newInstance(QueryDTO.class).getClass().getPackage().getName());
		} catch (JAXBException ex) {
			logger.severe(ex.toString());
		}
	}

	@Override
	public QueryDTO get() {
		List<ConstraintDTO> constraints = new ArrayList<>();
		IdentifierDTO ageIdentifier = new AlphaIdentifierDTO(Collections.singletonList("age"));
		ValueDTO ageValue = new IntegerValueDTO(35);
		constraints.add(new EqualDTO(ageIdentifier, ageValue));
		List<OrderDTO> orders = new ArrayList<>();
		IdentifierDTO nameIdentifier = new AlphaIdentifierDTO(Collections.singletonList("name"));
		orders.add(new OrderDTO(nameIdentifier, true));
		return new QueryDTO(constraints, orders, 0);
	}

	@Override
	public ResultContainerDTO query(QueryDTO queryDTO) {
		try {
			Query query = Transformer.convertQueryDTOtoQuery(queryDTO);
			logger.info("Retrieve posts using " + query);
			ResultContainer resultContainer = getSuccessor.get(query);
			return Transformer.convertResultContainertoResultContainerDTO(resultContainer);
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public AttributesDTO post(PostContainerDTO postContainer) {
		try {
			byte[] message = DatatypeConverter.parseBase64Binary(postContainer.getMessage());
			Attributes alpha = Transformer.convertAttributesDTOtoAttributes(postContainer.getAlpha());
			Attributes beta = new Attributes();
			logger.info("Post message=" + DatatypeConverter.printBase64Binary(message) + ", alpha=" + alpha + ", beta=" + beta);
			beta = postSuccessor.post(message, alpha, beta);
			return Transformer.convertAttributesToDTO(beta);
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
}
