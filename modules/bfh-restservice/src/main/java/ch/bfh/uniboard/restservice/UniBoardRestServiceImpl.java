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

import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.data.TransformException;
import ch.bfh.uniboard.data.Transformer;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.ResultContainer;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

/**
 * The class UniBoardRestServiceImpl implements a RESTful interface of the UniBoard by delegating the get and post
 * requests to the successor services.
 *
 * @author Stephan Fischli &lt;stephan.fischli@bfh.ch&gt;
 */
@Stateless(name = "UniBoardRestService")
public class UniBoardRestServiceImpl implements UniBoardRestService {

	protected static final Logger logger = Logger.getLogger(UniBoardRestServiceImpl.class.getName());

	@EJB
	private GetService getSuccessor;
	@EJB
	private PostService postSuccessor;

	@Override
	public ResultContainerDTO query(QueryDTO queryDTO) {
		try {
			Query query = Transformer.convertQueryDTOtoQuery(queryDTO);
			logger.log(Level.INFO, "Retrieve posts using {0}", query);
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
			byte[] message = Base64.getDecoder().decode(postContainer.getMessage());
			Attributes alpha = Transformer.convertAttributesDTOtoAttributes(postContainer.getAlpha());
			Attributes beta = new Attributes();
			logger.log(Level.INFO, "Post message={0}, alpha={1}, beta={2}",
					new Object[]{postContainer.getMessage(), alpha, beta});
			beta = postSuccessor.post(message, alpha, beta);
			return Transformer.convertAttributesToDTO(beta);
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
}
