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

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.webservice.data.AttributesDTO;
import ch.bfh.uniboard.webservice.data.AttributesDTO.EntryDTO;
import ch.bfh.uniboard.webservice.data.QueryDTO;
import ch.bfh.uniboard.webservice.data.ResultContainerDTO;
import java.util.Map;
import javax.ejb.EJB;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class UniBoardServiceImpl implements UniBoardService {

	@EJB
	private PostService postSuccessor;
	@EJB
	private GetService getSuccessor;

	@Override
	public ResultContainerDTO get(QueryDTO query) {

		Query q = new Query(null);

		this.getSuccessor.get(null);

		return null;
	}

	@Override
	public AttributesDTO post(byte[] message, AttributesDTO alpha) {

		Attributes alphaIntern = new Attributes();
		for (EntryDTO e : alpha.getEntry()) {
			alphaIntern.add(e.getKey(), e.getValue());
		}

		Attributes betaIntern = new Attributes();

		betaIntern = this.postSuccessor.post(message, alphaIntern, betaIntern);

		AttributesDTO beta = new AttributesDTO();
		for (Map.Entry<String, String> e : betaIntern.getEntries()) {
			EntryDTO ent = new EntryDTO();
			ent.setKey(e.getKey());
			ent.setValue(e.getValue());
			beta.getEntry().add(ent);
		}

		return beta;
	}

}
