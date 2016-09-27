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

import ch.bfh.uniboard.Get;
import ch.bfh.uniboard.GetResponse;
import ch.bfh.uniboard.PostResponse;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.ResultContainer;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.UniBoardService;
import ch.bfh.uniboard.data.*;
import ch.bfh.uniboard.service.*;
import ch.bfh.uniboard.Post;
import java.util.ArrayList;
import java.util.List;
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
	public GetResponse get(Get parameters) {

		try {
			Query q = Transformer.convertQueryDTOtoQuery(parameters.getQuery());

			ResultContainer rContainer = this.getSuccessor.get(q);

			return new GetResponse(Transformer.convertResultContainertoResultContainerDTO(rContainer));
		} catch (TransformException ex) {
			List<AttributeDTO> exAttributes = new ArrayList<>();
			AttributeDTO e = new AttributeDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(ex.getMessage());
			exAttributes.add(e);
			ResultContainerDTO exResultContainer = new ResultContainerDTO(new ArrayList<PostDTO>(), exAttributes);
			return new GetResponse(exResultContainer);
		}
	}

	@Override
	public PostResponse post(Post post) {

		try {
			Attributes alphaIntern = Transformer.convertDTOListtoAttributes(post.getAlpha());
			Attributes betaIntern = new Attributes();

			betaIntern = this.postSuccessor.post(post.getMessage(), alphaIntern, betaIntern);
			List<AttributeDTO> response = Transformer.convertAttributesToDTOList(betaIntern);
			return new PostResponse(response);

		} catch (TransformException ex) {
			List<AttributeDTO> exAttributes = new ArrayList<>();
			AttributeDTO e = new AttributeDTO();
			e.setKey(Attributes.ERROR);
			e.setValue(ex.getMessage());
			exAttributes.add(e);
			return new PostResponse(exAttributes);
		}
	}

}
