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
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author Stephan Fischli &lt;stephan.fischli@bfh.ch&gt;
 */
@Path("")
public interface UniBoardRestService {

<<<<<<< HEAD
	protected static final Logger logger = Logger.getLogger(UniBoardRestService.class.getName());

	@EJB
	private GetService getService;

	@EJB
	private PostService postService;
=======
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public QueryDTO get();
>>>>>>> 3c43d0662f3fe3c12031f15f9081deabb9a95c10

	@POST
	@Path("query")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ResultContainerDTO query(QueryDTO queryDTO);

	@POST
	@Path("post")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
<<<<<<< HEAD
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
=======
	public AttributesDTO post(PostContainerDTO postContainer);
>>>>>>> 3c43d0662f3fe3c12031f15f9081deabb9a95c10
}
