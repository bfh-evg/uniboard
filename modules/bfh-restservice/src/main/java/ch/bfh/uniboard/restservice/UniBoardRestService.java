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
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author Stephan Fischli &lt;stephan.fischli@bfh.ch&gt;
 */
@Path("")
@Stateless
public class UniBoardRestService {

	protected static final Logger logger = Logger.getLogger(UniBoardRestService.class.getName());

	@EJB
	private GetService getService;

	@EJB
	private PostService postService;

//	@GET
//	@Path("query")
//	@Produces(MediaType.APPLICATION_JSON)
//	public QueryDTO get() {
//		List<ConstraintDTO> constraints = new ArrayList<>();
//		IdentifierDTO ageIdentifier = new AlphaIdentifierDTO(Collections.singletonList("age"));
//		ValueDTO ageValue = new IntegerValueDTO(35);
//		constraints.add(new EqualDTO(ageIdentifier, ageValue));
//		List<OrderDTO> orders = new ArrayList<>();
//		IdentifierDTO nameIdentifier = new AlphaIdentifierDTO(Collections.singletonList("name"));
//		orders.add(new OrderDTO(nameIdentifier, true));
//		int limit = 0;
//		return new QueryDTO(constraints, orders, limit);
//	}
//
	@POST
	@Path("query")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResultContainerDTO get(QueryDTO queryDTO) {
		try {
			Query query = Transformer.convertQueryDTOtoQuery(queryDTO);
			logger.info("Retrieve posts, query=" + query);
			ResultContainer resultContainer = getService.get(query);
			return Transformer.convertResultContainertoResultContainerDTO(resultContainer);
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	@POST
	@Path("post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public AttributesDTO post(PostContainerDTO postContainer) {
		try {
			byte[] message = DatatypeConverter.parseBase64Binary(postContainer.getMessage());
			Attributes alpha = Transformer.convertAttributesDTOtoAttributes(postContainer.getAlpha());
			Attributes beta = new Attributes();
			logger.log(Level.INFO, "Post message={0}, alpha={1}, beta={2}", 
					new Object[]{DatatypeConverter.printBase64Binary(message), alpha, beta});
			beta = postService.post(message, alpha, beta);
			return Transformer.convertAttributesToDTO(beta);
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}
}
